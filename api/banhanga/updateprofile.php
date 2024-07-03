<?php
include 'connect.php';
$target_dir = "images/";
//=>
$id = $_POST['id'];
$email = $_POST['email'];
$username = $_POST['username'];
$file_name = $_POST['file_name'];
$mobile = $_POST['mobile'];
//=>
if (empty($file_name) || $file_name === "") {
    $file_name = "avatar.jpg";
} else {
    $file_name = $file_name . ".jpg";
}

$target_file_name = $target_dir . $file_name;

// Xử lý upload file
if (isset($_FILES["file"])) {
    if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file_name)) {
        $query = "UPDATE user SET username='" . $username . "',email='" . $email . "',avatar='" . $target_file_name . "',mobile='" . $mobile . "' WHERE id=$id";
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
print_r(json_encode(($arr)));
