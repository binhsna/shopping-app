<?php
include 'connect.php';
$tensp = $_POST['tensp'];
$gia = $_POST['gia'];
$hinhanh = $_POST['hinhanh'];
$mota = $_POST['mota'];
$loai = $_POST['loai'];
$sltonkho = $_POST['sltonkho'];
$id = $_POST['id'];
//=>
$query = "UPDATE sanpham SET tensp='" . $tensp . "', giasp='" . $gia . "', hinhanh='" . $hinhanh . "', mota='" . $mota . "', loai=$loai, sltonkho=$sltonkho WHERE id=$id";
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
