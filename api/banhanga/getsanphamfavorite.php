<?php
include 'connect.php';
$page = $_POST['page'];
$user_id = $_POST['user_id'];
$total = 10; // Cần lấy 10 sản phẩm trên 1 trang 
$pos = ($page - 1) * $total; // 0,10 10,10 -> Vị trí đầu tiên của trang

$query = "SELECT s.* FROM yeuthich as y, sanpham as s WHERE y.sp_id=s.id AND y.user_id=$user_id LIMIT $pos,$total";
$data = mysqli_query($conn, $query);

$result = array();
while ($row = mysqli_fetch_assoc($data)) {
    $result[] = ($row);
    // code ...
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
