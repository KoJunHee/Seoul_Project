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
    bucket: 'csr1994',
    acl: 'public-read',
    key: function (req, file, cb) {
      cb(null, Date.now() + '.' + file.originalname.split('.').pop());
    }
  })
});

// /article 글 저장 등록
router.post('/',upload.single('photo'), async (req, res, next) => {
  try {
      // let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
      // let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
      //
      // if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
      // else {
      // var decoded_pk = jwt.decode(token, {complete: true});

      var connection = await pool.getConnection();
      await connection.beginTransaction();

      let picture = {
        title: req.body.picture_title
        ////사진은 어떻게 저장??
      };

      let poem = {
        title: req.body.poem_title,
        content: req.body.cont
      };

      let setting = {
        font_type: req.body.font_type,
        font_size: req.body.font_size,
        bold: req.body.bold,
        inclination: req.body.inclination,
        underline: req.body.underline,
        color: req.body.color,
        sort: req.body.sort
      };



      let query1 = 'insert into seoul_poem.pictures set ?';
      await connection.query(query1, picture);

      let query1_1 = 'select idpictures from seoul_poem.pictures where title =?';
      let selected = await connection.query(query1_1, req.body.picture_title);
      console.log(selected[0].idpictures);

      let query2 = 'insert into seoul_poem.poem set ?';
      await connection.query(query2, poem);

      let query2_1 = 'select idpoem from seoul_poem.poem where title =?';
      let selected2 = await connection.query(query2_1, req.body.poem_title);
      console.log(selected2[0].idpoem);


      let query3 = 'insert into seoul_poem.setting set ?';
      await connection.query(query3, setting);

      let query3_1 = 'SELECT idsettings FROM seoul_poem.setting order by idsettings desc limit 1';
      let selected3 = await connection.query(query3_1);

      let article = {
        tags: req.body.tags,
        background: req.body.background,
        inform: req.body.inform,
        date: req.body.date,
        pictures_idpictures: selected[0].idpictures,
        poem_idpoem: selected2[0].idpoem,
        users_idusers: 1, //나중에 수정할 것!
        setting_idsettings: selected3[0].idsettings
      };


      let query4 = 'insert into seoul_poem.articles set ?';
      await connection.query(query4, article);

      res.status(201).send({result: "success"});
      await connection.commit();
      //}
    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});

// 글 수정
router.put('/:idarticles',upload.single('photo'), async (req, res, next) => {
  try {
        var connection = await pool.getConnection();

        let picture = {
          title: req.body.picture_title
          ////사진은 어떻게 저장??
        };

        let poem = {
          title: req.body.poem_title,
          content: req.body.content
        };

        let setting = {
          font_type: req.body.font_type,
          font_size: req.body.font_size,
          bold: req.body.bold,
          inclination: req.body.inclination,
          underline: req.body.underline,
          color: req.body.color,
          sort: req.body.sort
        };

        let query1 = 'select pictures_idpictures, poem_idpoem, setting_idsettings from seoul_poem.articles where idarticles = ?';
        let selected = await connection.query(query1, req.params.idarticles);


        let query2 = 'update seoul_poem.pictures set ? where idpictures = ?';
        await connection.query(query2, [picture, selected[0].pictures_idpictures]);

        let query3 = 'update seoul_poem.poem set ? where idpoem = ?';
        await connection.query(query3, [poem, selected[0].poem_idpoem]);

        let query4 = 'update seoul_poem.setting set ? where idsettings = ?';
        await connection.query(query4, [setting, selected[0].setting_idsettings]);


        res.status(201).send({result: "update article success"});

    }
    catch(err) {
        console.log(err);
        res.status(500).send({result: err });
    }
    finally {
        pool.releaseConnection(connection);
    }
});


//글 하나 조회 /article/{articleid}
router.get('/:idarticles', async (req, res) => {
    try {
        //
        // let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
        // let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
        //
        // if(!decoded) res.status(400).send({ result: 'wrong token '}); //유효하지 않다면 메시지를 보냅니다.
        // else {
        // var decoded_pk = jwt.decode(token, {complete: true});

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT * FROM seoul_poem.articles, seoul_poem.poem, seoul_poem.pictures, seoul_poem.setting where seoul_poem.articles.idarticles=? and seoul_poem.articles.poem_idpoem=seoul_poem.poem.idpoem and seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.setting_idsettings=seoul_poem.setting.idsettings';
        let article_list = await connection.query(query1, req.params.idarticles);

        res.status(200).send( { article_list: article_list });
        await connection.commit();
        //}

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

module.exports = router;
