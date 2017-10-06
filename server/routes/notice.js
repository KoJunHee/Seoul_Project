const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');

//공지 전체 목록
router.get('/', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT idnotices, title, content FROM seoul_poem.notices;';
        let notice_list = await connection.query(query1);

        res.status(200).send( { notice_list: notice_list });
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

//공지 하나 조회
router.get('/:idnotices', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT * FROM seoul_poem.notices where idnotices = ?';
        let notice_list = await connection.query(query1, req.params.idnotices);

        res.status(200).send( { notice_list: notice_list });
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


module.exports = router;
