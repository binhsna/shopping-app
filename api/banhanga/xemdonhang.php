<?php
include 'connect.php';
$user_id = $_POST['user_id'];

if ($user_id == 0) {
    // get all đơn hàng
    $query = "SELECT donhang.id AS id, donhang.iduser, donhang.diachi, donhang.sodienthoai, donhang.email, donhang.soluong, donhang.tongtien, donhang.trangthai, donhang.online, donhang.ngaydat, user.username FROM donhang INNER JOIN user ON donhang.iduser=user.id ORDER BY donhang.id DESC";
} else {
    $query = "SELECT * FROM donhang WHERE iduser=$user_id ORDER BY id DESC";
}
$data = mysqli_query($conn, $query);
$result = array();
while ($row = mysqli_fetch_assoc($data)) {
    $truyvan = "SELECT * FROM chitietdonhang INNER JOIN sanpham on chitietdonhang.idsp=sanpham.id WHERE chitietdonhang.iddonhang=" . $row['id'];
    $data1 = mysqli_query($conn, $truyvan);
    $item = array();
    while ($row1 = mysqli_fetch_assoc($data1)) {
        $item[] = $row1;
    }
    $row['item'] = $item;

    $result[] = ($row);
}
if (!empty($result)) {
    $arr = [
        'success' => true,
        'message' => 'Thành công',
        'result' => $result
    ];
} else {
    $arr = [
        'success' => false,
        'message' => 'Thất bại',
        'result' => $result
    ];
}
print_r(json_encode(($arr)));