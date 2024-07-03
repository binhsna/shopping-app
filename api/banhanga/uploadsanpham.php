<?php
include 'connect.php';
$target_dir = "images/";
// Đặt tên cho file ảnh
$query = "select max(id) as id from sanpham";
$data = mysqli_query($conn, $query);
$result = array();
while ($row = mysqli_fetch_assoc($data)) {
    $result[] = ($row);
    // Code ...
}
if ($result[0]['id'] == null) {
    $name = 1;
} else {
    $name = ++$result[0]['id'];
}
$name = $name . ".jpg";
$target_file_name = $target_dir . $name;

// Xử lý upload file
if (isset($_FILES["file"])) {
    if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file_name)) {
        $arr = [
            'success' => true,
            'message' => 'Thành công',
            'name' => $name
        ];
    } else {
        $arr = [
            'success' => false,
            'message' => 'Thất bại',
        ];
    }
} else {
    $arr = [
        'success' => false,
        'message' => 'Thất bại',
    ];
}

echo json_encode($arr);
