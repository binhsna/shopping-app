<?php
include 'connect.php';
$page = $_POST['page'];
$search = $_POST['search'];
$total = 8; // Số sản phẩm cần lấy trên mỗi trang
$pos = ($page - 1) * $total; // Vị trí đầu tiên của trang

$query = "";
if (empty($search) || $search === "") {
    $query = "SELECT * FROM sanpham LIMIT ?, ?";
} else {
    $query = "SELECT * FROM sanpham WHERE tensp LIKE ? LIMIT ?, ?";
}

$stmt = mysqli_prepare($conn, $query);
if ($stmt) {
    if (empty($search) || $search === "") {
        mysqli_stmt_bind_param($stmt, "ii", $pos, $total);
    } else {
        $searchParam = "%" . $search . "%";
        mysqli_stmt_bind_param($stmt, "sii", $searchParam, $pos, $total);
    }
    mysqli_stmt_execute($stmt);
    $data = mysqli_stmt_get_result($stmt);
    mysqli_stmt_close($stmt);
}
