<?php
include 'connect.php';
$page = $_POST['page'];
$search = $_POST['search'];
$total = 8; // Cần lấy số sản phẩm trên 1 trang 
$pos = ($page - 1) * $total; // Vị trí đầu tiên của trang
//=>
$query = "";
if (empty($search) || $search === "") {
    $query = "SELECT * FROM sanpham LIMIT $pos,$total";
} else {
    $query = "SELECT * FROM sanpham WHERE tensp LIKE '%" . $search . "%' LIMIT $pos,$total";
}
$data = mysqli_query($conn, $query);

$result = array();
while ($row = mysqli_fetch_assoc($data)) {
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
        'message' => 'Vui lòng nhập sản phẩm muốn tìm',
        'result' => $result
    ];
}
print_r(json_encode(($arr)));
