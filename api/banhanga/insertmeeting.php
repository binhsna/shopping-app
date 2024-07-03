<?php
include 'connect.php';
$meetingId = $_POST['meetingId'];
$token = $_POST['token'];
//=>
$query = "INSERT INTO meeting(meetingId, token) VALUES ('" . $meetingId . "'," . "'$token')";
$data = mysqli_query($conn, $query);
//=>
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
print_r(json_encode(($arr)));
