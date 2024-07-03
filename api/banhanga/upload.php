<?php
$target_dir = "images/";
// Lấy về tên của file ảnh
$name = $_POST['file_name'];

if (empty($name) || $name === "") {
    $name = "avatar.jpg";
} else {
    $name = $name . ".jpg";
}

$target_file_name = $target_dir . $name;

// Xử lý upload file
if (isset($_FILES["file"])) {
    if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file_name)) {
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
} else {
    $arr = [
        'success' => false,
        'message' => 'Thất bại',
    ];
}

echo json_encode($arr);
