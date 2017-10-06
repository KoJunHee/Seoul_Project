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


router.post('/',async (req, res) => {
    try{
        var connection = await pool.getConnection();
        await connection.beginTransaction();
        var id = req.params.users_idusers;

    }catch (err){

    }finally {

    }
});


module.exports = router;