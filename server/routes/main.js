const express = require('express');
const router = express.Router();
const pool = require('../config/db_pool');
const jwt = require('jsonwebtoken');

    router.get('/:tag', async (req, res) => {
    try{
        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let tag = req.params.tag;
        console.log(req.params.tag);

        let query1 = 'select * from seoul_poem.pictures, seoul_poem.articles where seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.tags like '+'\'%'+tag+'%\''+' order by rand() limit 5;'

        console.log(query1);
        let main_list = await connection.query(query1);

        res.status(200).send( { main_list : main_list });

        await connection.commit();

    }catch(err){
        console.log(err);
        res.status(500).send( { result: err });
        connection.rollback();

    }finally{
        pool.releaseConnection(connection);
    }
});

//태그내에 모든 사진들을 보여줌

router.post('/', async ( req, res) => {
    try{

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let tag = req.body.tag;

        console.log(req.body.tag);
        console.log(req.body);
        let query1 = "select * from seoul_poem.pictures, seoul_poem.articles where seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.tags like "+'\'%'+tag+'%\''+ " order by seoul_poem.articles.idarticles Desc;"

        let main_list = await connection.query(query1);

        res.status(200).send( { main_list : main_list });

        await connection.commit();

    }catch(err){
        console.log(err);
        res.status(500).send( { result: err });
        connection.rollback();

    }finally{
        pool.releaseConnection(connection);
    }
});



//태그나 작가별 검색에 이용 (3개씩 보여주며)

router.post('/search',async(req, res) => {

    try{

        var connection = await pool.getConnection();
        await connection.beginTransaction();

        let tag = req.body.tag;

        let query1 = "select * from seoul_poem.articles, seoul_poem.poem, seoul_poem.pictures where seoul_poem.articles.poem_idpoem=seoul_poem.poem.idpoem and seoul_poem.articles.pictures_idpictures=seoul_poem.pictures.idpictures and seoul_poem.articles.tags like "+'\'%'+tag+'%\''+" order by seoul_poem.articles.idarticles Desc limit 3;"
        let query2 = "select * from seoul_poem.users where seoul_poem.users.pen_name like "+'\'%'+tag+'%\''+" and seoul_poem.users.author = 1 order by seoul_poem.users.idusers Desc limit 3"

        let article_list = await connection.query(query1);
        let author_list = await connection.query(query2);

        console.log(query1);
        console.log(query2);

        res.status(200).send( { author_list : author_list , article_list : article_list});
        await connection.commit();


    }catch(err){
        console.log(err);
        res.status(500).send( { result: err });
        connection.rollback();

    }
    finally {
        pool.releaseConnection(connection);
    }
});

module.exports = router;
