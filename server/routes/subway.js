const express = require('express');
const aws = require('aws-sdk');
const moment = require('moment');
const jwt = require('jsonwebtoken');
const multer = require('multer');
const multerS3 = require('multer-s3');
const router = express.Router();
aws.config.loadFromPath('./config/aws_config.json');
const pool = require('../config/db_pool');

//지하철시 전체 목록
router.get('/', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT idsubway, title FROM seoul_poem.subway_poem;';
        let subway_list = await connection.query(query1);

        res.status(200).send( { subway_list: subway_list });
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

//지하철시 하나 조회
router.get('/:idsubway', async (req, res) => {
    try {
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let query1 = 'SELECT * FROM seoul_poem.subway_poem where idsubway = ?';
        let subway_list = await connection.query(query1, req.params.idsubway);

        res.status(200).send( { subway_list: subway_list });
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
