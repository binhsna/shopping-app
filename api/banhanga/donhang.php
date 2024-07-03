<?php
include 'connect.php';
$sdt = $_POST['sdt'];
$email = $_POST['email'];
$tongtien = $_POST['tongtien'];
$iduser = $_POST['iduser'];
$diachi = $_POST['diachi'];
$soluong = $_POST['soluong'];
$chitiet = $_POST['chitiet'];


$query = "INSERT INTO donhang(iduser, diachi, sodienthoai, email, soluong, tongtien) VALUES ($iduser,'$diachi','$sdt','$email',$soluong,'$tongtien')";
$data = mysqli_query($conn, $query);

if ($data == true) {
    $query = "SELECT id AS iddonhang FROM donhang WHERE iduser=$iduser ORDER BY id DESC LIMIT 1";
    $data = mysqli_query($conn, $query);
    $result = array();
    while ($row = mysqli_fetch_assoc($data)) {
        $result = ($row);
    }
    if (!empty($result)) {
        // Có đơn hàng
        $chitiet = json_decode($chitiet, true);
        foreach ($chitiet as $key => $value) {
            $truyvan = "INSERT INTO chitietdonhang(iddonhang, idsp, soluong, gia) VALUES (" . $result['iddonhang'] . "," . $value['idsp'] . "," . $value['soluong'] . "," . "'" . $value['giasp'] . "'"  . ")";
            $data = mysqli_query($conn, $truyvan);

            // Xử lý cập nhật lại số lượng tồn kho
            $truyvankho = "SELECT sltonkho FROM sanpham WHERE id=" . $value['idsp'];
            $data1 = mysqli_query($conn, $truyvankho);
            $sltrenkho = mysqli_fetch_assoc($data1);

            $slupdate = $sltrenkho['sltonkho'] - $value['soluong'];

            $truyvankho2 = "UPDATE sanpham SET sltonkho=$slupdate WHERE id=" . $value['idsp'];
            $data2 = mysqli_query($conn, $truyvankho2);
        }

        if ($data == true) {
            $arr = [
                'success' => true,
                'message' => 'Thành công',
                'iddonhang' => $result['iddonhang']
            ];
        } else {
            $arr = [
                'success' => false,
                'message' => 'Không thành công'
            ];
        }
        print_r(json_encode(($arr)));
    }
} else {
    $arr = [
        'success' => false,
        'message' => 'Không thành công'
    ];
    print_r(json_encode(($arr)));
}
