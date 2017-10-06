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
const bcrypt = require('bcryptjs'); //해쉬용 확장모듈, 윈도우에서 돌릴때는 js 빼고 돌려야 함

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
const saltRounds = 10;

//회원가입
router.post('/',upload.single('photo'), function(req, res){
  let query = 'select * from user where email = ?';
  pool.getConnection(function(err, connection){

    if(err) console.log('getConection error:', err);
    else {
      connection.query(query , req.body.email, function(err, data){
        if(err){
          console.log("first query err: ", err);
          connection.release();
        }
        else{
          if(data.length!==0) res.status(406).send({ result : "이미 존재하는 계정입니다."});

          else{
          bcrypt.hash(req.body.pw, saltRounds, function(err, hashed){
            if(err) console.log("Hashing error", err);
            else{

              let user = {
                email: req.body.email,
                pw: hashed,
                name: req.body.name,
                ph: req.body.ph,
                photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
              };
              let query2 = 'insert into user set ?';
              connection.query(query2, user, function(err, result){
                    if(err){
                      console.log('second query err', err);
                      connection.release();
                    }
                    else{
                      res.status(201).send({result: '회원가입 완료'});
                    }
                    connection.release();
              });
            }
          });
          }
        }
      });
    }
  });
});

//로그인
router.post('/login', function(req, res){
  pool.getConnection(function(err, connection){
    if(err) console.log('getConnection err : ', err );
    else{
      const email = req.body.email;
      const pw = req.body.pw;
      let query = 'select * from managerNa_aj.user where email = ?';
        connection.query(query , req.body.email, function(err, data){ // injection 방어 : +가 아니라 , 를 추가
          if(err){
            console.log("query err: ", err);
            connection.release();
          }
          else{
            if(data.length>0){
              bcrypt.compare(req.body.pw, data[0].pw, function(err, result) {
                  if (err)
                      console.log("compare error", err);
                  else {
                      if (result) {
                        bcrypt.hash(data[0].pw, saltRounds, function(err, hash){
                          if(err) console.log("Hashing error", err);
                          else console.log("myPassword hashed", hash);
                          //jwt 발급하고 성공메세지 보내주기
                          let option = {
                            algorithm : 'HS256',
                            expiresIn : 60 * 60 * 24 * 30 //토큰만료기간 : 한달
                          };
                          let payload = {
                            pk: data[0].pk
                          };
                          let token = jwt.sign(payload, req.app.get('jwt-secret'), option);
                          res.status(200).send({token : token});

                        });
                      } else {
                        res.status(402).send({result: '아이디나 비밀번호가 올바르지 않습니다'});
                      }
                  }
              });
            }
            else{
                res.status(403).send({result: '존재하지 않는 아이디입니다. 먼저 가입해주세요'});
            }
            connection.release();
          }
        });
      }
  });
});



// 회원정보 수정
router.put('/',upload.single('photo'), function(req, res){
  pool.getConnection(function(err, connection){

    if(err) console.log('getConection error:', err);
    else {
      let token = req.headers.token; //클라이언트에서 헤더에 담아 보낸 토큰을 가져옵니다.
      let decoded = jwt.verify(token, req.app.get('jwt-secret')); //보낸 토큰이 유효한 토큰인지 검증합니다.(토큰 발급 시 사용했던 key로);
      var decoded_pk = jwt.decode(token, {complete: true});

          bcrypt.hash(req.body.pw, saltRounds, function(err, hashed){
            if(err) console.log("Hashing error", err);
            else{

              let user = {
                pw: hashed,
                ph: req.body.ph,
                photo: req.file ? req.file.location : null //요청 값에 파일이 있으면 파일의 주소를, 그렇지 않으면 null.
              };

              let query2 = 'update managerNa_aj.user set ? where pk = ?';
              connection.query(query2, [user,decoded_pk.payload.pk] ,function(err, result){
                    if(err){
                      console.log('second query err', err);
                      connection.release();
                    }
                    else{
                      res.status(201).send({result: '회원정보 수정 완료'});
                    }
                    connection.release();
              });
            }
          });
    }
  });
});

module.exports = router;
