<?php
include 'connect.php';
$sp_id = $_POST['sp_id'];
$user_id = $_POST['user_id'];
$action = $_POST['action'];
//=> Nếu action là add thì thêm, remove là xóa
$query = "";
$message = "";
if ($action == "add") {
    $query = "INSERT INTO yeuthich(sp_id, user_id) VALUES ($sp_id,$user_id)";
    $message = "Thêm yêu thích thành công!";
} else if ($action == "remove") {
    $query = "DELETE FROM yeuthich WHERE sp_id=$sp_id AND user_id=$user_id";
    $message = "Xóa yêu thích thành công!";
}
$data = mysqli_query($conn, $query);

if ($data == true) {
    $arr = [
        'success' => true,
        'message' => $message,
    ];
} else {
    $arr = [
        'success' => false,
        'message' => 'Thất bại',
    ];
}
print_r(json_encode(($arr)));
