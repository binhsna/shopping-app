<?php
include 'connect.php';
$iddonhang = $_POST['iddonhang'];
$token = $_POST['token'];

//
$query = "UPDATE donhang SET online='$token' WHERE id=$iddonhang";
$data = mysqli_query($conn, $query);

if ($data == true) {
    $arr = [
        'success' => true,
        'message' => 'Thành công',
    ];
} else {
    $arr = [
        'success' => false,
        'message' => 'Thất bại',
    ];
}
print_r(json_encode(($arr)));
