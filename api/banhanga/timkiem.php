<?php
include 'connect.php';
$search = $_POST['search'];
if (empty($search) || $search === "") {
    $arr = [
        'success' => false,
        'message' => 'Không thành công',
    ];
} else {
    $query = "SELECT * FROM sanpham WHERE tensp LIKE '%" . $search . "%'";
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
}
print_r(json_encode(($arr)));