<?php
/*
$host = 'localhost';
$user = 'root';
$pass = '';
$database = 'banhanga';
*/
//=>
$host = 'sql202.liveblog365.com';
$user = 'lblog_35002472';
$pass = '1p56iyuzpxmkwjo';
$database = 'lblog_35002472_banhang';

$conn = mysqli_connect($host, $user, $pass, $database);
mysqli_set_charset($conn, 'utf8');
