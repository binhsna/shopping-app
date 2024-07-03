<?php
include 'connect.php';
$user_id = $_POST['user_id'];
$query = "SELECT * FROM yeuthich WHERE user_id=$user_id ORDER BY sp_id ASC";
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
