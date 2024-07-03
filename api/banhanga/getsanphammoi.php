<?php
include 'connect.php';
$page = $_POST['page'];
$total = 4; // Cần lấy số sản phẩm trên 1 trang 
$pos = ($page - 1) * $total; // Vị trí đầu tiên của trang

$query = "SELECT * FROM sanpham WHERE moi=1 LIMIT $pos,$total";
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