<?php
include 'connect.php';
$id = $_POST['iddonhang'];

//
$query = "DELETE FROM chitietdonhang WHERE iddonhang=$id";
$data = mysqli_query($conn, $query);

$query = "DELETE FROM donhang WHERE id=$id";
$data = mysqli_query($conn, $query);

if ($data == true) {
    $arr = [
        'success' => true,
        'message' => 'Xóa thành công',
    ];
} else {
    $arr = [
        'success' => false,
        'message' => 'Xóa thất bại',
    ];
}
print_r(json_encode(($arr)));
