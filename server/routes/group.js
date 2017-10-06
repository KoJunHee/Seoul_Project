const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');
const s3 = new aws.S3();

const upload = multer({
  storage: multerS3({
    s3: s3,
    bucket: 'sopt20.server.seminar6',
    acl: 'public-read',
    key: function (req, file, cb) {
      cb(null, Date.now() + '.' + file.originalname.split('.').pop());
    }
  })
});


//group_id로 검색한 모임 & 그 모임의 행사 정보 보여주기
router.get('/:group_id', async (req, res) => {
    try {

        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
      // get the decoded payload and header
        var decoded = jwt.decode(token, {complete: true});


        var connection = await pool.getConnection();
        await connection.beginTransaction();


        let query1 = 'SELECT managerNa_aj.group.id, managerNa_aj.group.title, managerNa_aj.group.text, managerNa_aj.group.photo, COUNT(managerNa_aj.group_has_user.user_id) as member_count, managerNa_aj.group_has_user.is_chairman, managerNa_aj.group.pw, managerNa_aj.group_has_user.is_new, managerNa_aj.group.chairman_name  FROM user,group_has_user,managerNa_aj.group WHERE managerNa_aj.group.id = group_has_user.group_id and group_has_user.user_id = user.pk and managerNa_aj.group.id=? and user.pk = ?;';
        let group = await connection.query(query1, [req.params.group_id , decoded.payload.pk]);
        if (group.length == 0) res.status(405).send({result:'해당 id를 가진 그룹이 존재하지 않습니다.'});
        let query2 = 'select event.id, managerNa_aj.event.title, managerNa_aj.event.place, event.start_date, managerNa_aj.event.end_date, event.photo, event.amount from event where  group_id in (select id from managerNa_aj.group where managerNa_aj.group.id = ?);';
        let events = await connection.query(query2, req.params.group_id);


        function repeater4(i){//node는 비동기화이기때문에 반복문을 사용하기위해 재귀함수를 이용함
          if(i<events.length)//받아온 행사길이 만큼 재귀 돌려버림~
          {

              connection.query('select managerNa_aj.user_has_event.is_manager from managerNa_aj.user_has_event where user_has_event.event_id = ? and user_has_event.user_pk = ?',[events[i].id ,decoded.payload.pk],function(error,row){
              if(error){
                console.log("connection error"+error);
                res.status(500).send({result:'FAIL'});
              }
              else{
                if(row.length==0)  events[i]['is_manager'] = 0;
                else  events[i]['is_manager'] = row[0]['is_manager'];
              }
              repeater4(i+1);
            });
          }
          if(i == events.length)//반복문이 끝났을 시
          {
                res.status(200).send( { group: group , events: events });
          }
        }
        repeater4(0);



        let query3 = 'update managerNa_aj.group_has_user set group_has_user.is_new = group_has_user.is_new-1 where managerNa_aj.group_has_user.user_id = ? and group_has_user.group_id = ?';

        if(group[0].is_new==1){
           await connection.query(query3, [ decoded.payload.pk , req.params.group_id]);
        }
        console.log("query3")


        await connection.commit();


    }
    catch(err){
        console.log(err);
        res.status(500).send( { result: err });
        connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }

});
// 모임 만들기
router.post('/',upload.single('photo'), async (req, res, next) => {
  try {
      let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
      let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);

      if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
      else {
      var decoded_pk = jwt.decode(token, {complete: true});

      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let group = {
        title: req.body.title,
        text: req.body.text,
        pw: req.body.pw,
        photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
      };

      let query = 'SELECT * FROM managerNa_aj.group where title =? ';
      let data = await connection.query(query, req.body.title) || null;
      if(data.length>0) res.status(406).send({result: '이미 존재하는 그룹 이름입니다.'});

      let query1 = 'insert into managerNa_aj.group set ?';
      await connection.query(query1, group);

      let query2 = 'select id from managerNa_aj.group where title =? and pw=?';
      let selected = await connection.query(query2, [req.body.title, req.body.pw]);
      console.log(selected[0].id);

      let group_has_user ={
        group_id : selected[0].id,
        user_id : decoded_pk.payload.pk,
        is_chairman : 1

      };



      let query3 = 'insert into managerNa_aj.group_has_user set ?';
      await connection.query(query3, group_has_user);


      res.status(201).send({result: "add group success"});
      await connection.commit();
      }
    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});


// 모임 정보 수정
router.put('/:group_id',upload.single('photo'), async (req, res, next) => {
  try {
        var connection = await pool.getConnection();
        let group_id = req.params.group_id;

        let group = {
          title: req.body.title,
          text: req.body.text,
          pw: req.body.pw,
          photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
        };

        let query = 'update managerNa_aj.group set ? where id = ?';
        let updated = await connection.query(query, [group, group_id]);
        console.log(updated);
        res.status(201).send({result: "update group success"});

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// delete /group/:group_id : 파라미터로 들어온 id 값을 가진 모임 정보 삭제
router.delete('/:group_id', async (req, res, next) => {
  try {
      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let query = 'SELECT * FROM managerNa_aj.group where id=? ';
      let data1 = await connection.query(query, req.params.group_id) || null;
      if(data1.length==0) res.status(403).send({result: '존재하지 않는 그룹 id입니다.'});

      let query1 = 'delete from managerNa_aj.group_has_user where group_id = ?';
      await connection.query(query1, req.params.group_id);
      let query2 = 'delete from managerNa_aj.group where id = ?';
      await connection.query(query2, req.params.group_id);

      res.status(200).send({result: 'delete success'});
      await connection.commit();
  }
  catch(err){
      console.log(err);
      res.status(500).send( { result: err });
      connection.rollback();
  }
  finally{
      pool.releaseConnection(connection);
  }
});

//모임 이름으로 검색
router.get('/', async (req, res) => {
    try {
        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
        if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
        else {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let groups = await connection.query("select managerNa_aj.group.id, title, text, photo, chairman_name, COUNT(managerNa_aj.group_has_user.user_id) as count FROM managerNa_aj.group , group_has_user WHERE id in (select id from managerNa_aj.group where managerNa_aj.group.title like '%" + req.query.title + "%') and group_has_user.group_id = group.id;");

        if(groups.length == 0 ) res.status(405).send( {result: "검색한 모임이 존재하지 않습니다." });

        res.status(200).send( { groups: groups });
        await connection.commit();
        }

    }
    catch(err){
        console.log(err);
        res.status(500).send( { result: err });
        connection.rollback();
    }
    finally{
        pool.releaseConnection(connection);
    }

});

// 모임 참여하기
router.post('/join/:group_id', async (req, res, next) => {
  try {
        let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
        var decoded_pk = jwt.decode(token, {complete: true});

        var connection = await pool.getConnection();
        let id = req.params.group_id;
        let pw = req.body.pw;
        let query1 = 'select * from managerNa_aj.group where id = ?';
        let group_info = await connection.query(query1, id) || null;


        if(group_info.length==0) res.status(405).send({result: '존재하지 않는 그룹 id입니다.'});
        let query1_1 = 'select * from  managerNa_aj.group_has_user where group_has_user.group_id = ? and group_has_user.user_id = ?';
        let data = await connection.query(query1_1, [id, decoded_pk.payload.pk]);
        if(data.length>0) res.status(406).send({result: '이미 참여한 모임입니다.'});
        else {
          if(pw!=group_info[0].pw) res.status(401).send({result: 'wrong password'});
            else {
              let query2 = 'insert into managerNa_aj.group_has_user set ?';
              let record ={
                group_id : id,
                user_id : decoded_pk.payload.pk,  //user.pk로 바꿔야됌
                is_chairman : 0
              };
              let inserted = await connection.query(query2, record);
              console.log(inserted);
              res.status(201).send({result: "apply group success"});
              }
          }
    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});




module.exports = router;
