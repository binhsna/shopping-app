-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 05, 2024 at 06:57 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `banhanga`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_multiple_records` ()   BEGIN
  DECLARE counter INT DEFAULT 1;

  WHILE counter <= 31 DO
    INSERT INTO hinhanh (id_sp, url) VALUES (counter, '');
    SET counter = counter + 1;
  END WHILE;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `chitietdonhang`
--

CREATE TABLE `chitietdonhang` (
  `iddonhang` int(11) NOT NULL,
  `idsp` int(11) NOT NULL,
  `soluong` int(11) NOT NULL,
  `gia` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `chitietdonhang`
--

INSERT INTO `chitietdonhang` (`iddonhang`, `idsp`, `soluong`, `gia`) VALUES
(1, 1, 3, '75'),
(1, 3, 2, '75'),
(2, 1, 3, '75'),
(2, 3, 2, '75'),
(3, 1, 3, '75'),
(3, 3, 2, '75'),
(4, 1, 3, '75'),
(4, 3, 2, '75'),
(5, 1, 2, '75'),
(5, 2, 3, '100'),
(5, 25, 3, '65'),
(5, 42, 1, '35'),
(5, 61, 5, '25'),
(41, 1, 3, '75'),
(41, 3, 2, '75'),
(42, 1, 3, '75'),
(42, 3, 2, '75'),
(43, 1, 3, '75'),
(43, 3, 2, '75'),
(44, 1, 3, '75'),
(44, 3, 2, '75');

-- --------------------------------------------------------

--
-- Table structure for table `donhang`
--

CREATE TABLE `donhang` (
  `id` int(11) NOT NULL,
  `iduser` int(11) NOT NULL,
  `diachi` text NOT NULL,
  `sodienthoai` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `soluong` int(11) NOT NULL,
  `tongtien` varchar(255) NOT NULL,
  `trangthai` int(2) NOT NULL DEFAULT 0,
  `online` text DEFAULT NULL,
  `ngaydat` date NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `donhang`
--

INSERT INTO `donhang` (`id`, `iduser`, `diachi`, `sodienthoai`, `email`, `soluong`, `tongtien`, `trangthai`, `online`, `ngaydat`) VALUES
(1, 2, 'Tổ 7, Yên Nghĩa, Hà Đông', '+84971912776', 'binh19964@huce.edu.vn', 5, '348', 0, NULL, '2023-12-30'),
(2, 2, 'Tổ 7, Yên Nghĩa, Hà Đông', '+84971912776', 'binh19964@huce.edu.vn', 5, '348', 0, NULL, '2023-12-30'),
(3, 2, 'Hà Nội', '+84971912776', 'binh19964@huce.edu.vn', 10, '348', 0, NULL, '2023-12-30'),
(4, 2, 'Hà Đông', '+84971912776', 'binh19964@huce.edu.vn', 5, '348', 0, NULL, '2023-12-30'),
(5, 2, 'Tổ 7, phường Yên Nghĩa, Hà Đông, Hà Nội', '+84971912776', 'binh19964@huce.edu.vn', 14, '731', 0, NULL, '2023-12-18'),
(41, 2, 'Tổ 7, Yên Nghĩa, Hà Đông', '+84971912776', 'binh19964@huce.edu.vn', 5, '348', 0, NULL, '2023-12-30'),
(42, 2, 'Hà Đông', '+84971912776', 'binh19964@huce.edu.vn', 5, '348', 0, NULL, '2023-12-30'),
(43, 2, 'Hà Đông', '+84971912776', 'binh19964@huce.edu.vn', 5, '348', 0, NULL, '2023-12-30'),
(44, 2, 'Hà Nội', '+84971912776', 'binh19964@huce.edu.vn', 5, '348', 0, NULL, '2023-12-30');

-- --------------------------------------------------------

--
-- Table structure for table `hinhanh`
--

CREATE TABLE `hinhanh` (
  `id` int(11) NOT NULL,
  `id_sp` int(11) NOT NULL,
  `url` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `hinhanh`
--

INSERT INTO `hinhanh` (`id`, `id_sp`, `url`) VALUES
(1, 1, 'https://ngochieu.name.vn/img/home.png'),
(2, 1, 'https://ngochieu.name.vn/img/home.png'),
(3, 1, 'https://ngochieu.name.vn/img/home.png'),
(4, 2, 'https://ngochieu.name.vn/img/home.png'),
(5, 2, 'https://ngochieu.name.vn/img/home.png'),
(6, 2, 'https://ngochieu.name.vn/img/home.png'),
(7, 3, 'https://ngochieu.name.vn/img/home.png'),
(8, 3, 'https://ngochieu.name.vn/img/home.png'),
(9, 3, 'https://ngochieu.name.vn/img/home.png'),
(10, 4, 'https://ngochieu.name.vn/img/home.png'),
(11, 4, 'https://ngochieu.name.vn/img/home.png'),
(12, 4, 'https://ngochieu.name.vn/img/home.png'),
(13, 5, 'https://ngochieu.name.vn/img/home.png'),
(14, 5, 'https://ngochieu.name.vn/img/home.png'),
(15, 5, 'https://ngochieu.name.vn/img/home.png'),
(16, 6, 'https://ngochieu.name.vn/img/home.png'),
(17, 6, 'https://ngochieu.name.vn/img/home.png'),
(18, 6, 'https://ngochieu.name.vn/img/home.png'),
(19, 7, 'https://ngochieu.name.vn/img/home.png'),
(20, 7, 'https://ngochieu.name.vn/img/home.png'),
(21, 7, 'https://ngochieu.name.vn/img/home.png'),
(22, 8, 'https://ngochieu.name.vn/img/home.png'),
(23, 8, 'https://ngochieu.name.vn/img/home.png'),
(24, 8, 'https://ngochieu.name.vn/img/home.png'),
(25, 9, 'https://ngochieu.name.vn/img/home.png'),
(26, 26, 'https://ngochieu.name.vn/img/home.png'),
(27, 27, 'https://ngochieu.name.vn/img/home.png'),
(28, 28, 'https://ngochieu.name.vn/img/home.png'),
(29, 29, 'https://ngochieu.name.vn/img/home.png'),
(30, 30, 'https://ngochieu.name.vn/img/home.png'),
(31, 31, 'https://ngochieu.name.vn/img/home.png');

-- --------------------------------------------------------

--
-- Table structure for table `khuyenmai`
--

CREATE TABLE `khuyenmai` (
  `id` int(11) NOT NULL,
  `url` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `thongtin` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `khuyenmai`
--

INSERT INTO `khuyenmai` (`id`, `url`, `thongtin`) VALUES
(1, 'http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png', 'Chương trình khuyến mãi tháng 9 với nhiều ưu đãi trong mùa tựu trường'),
(2, 'http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png', 'Chương trình giảm giá tân sinh viên'),
(3, 'http://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-big-ky-nguyen-800-300.jpg', 'Chương trình giảm giá siêu hot');

-- --------------------------------------------------------

--
-- Table structure for table `meeting`
--

CREATE TABLE `meeting` (
  `id` int(11) NOT NULL,
  `meetingId` text NOT NULL,
  `token` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `meeting`
--

INSERT INTO `meeting` (`id`, `meetingId`, `token`) VALUES
(1, 'sh2k-v69i-t4xn', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI3NGQwMDUwYy0xNTFiLTQxYTMtOWVjOS1jNjIwMTRkMzViNjEiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY5NDc3MzQ5NCwiZXhwIjoxNzI2MzA5NDk0fQ.NqR2FmgNgaaLRltlQfuKk4aEImfgEc8h5a3vHjvue3w'),
(2, 'pawv-0kmg-vj3n', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI3NGQwMDUwYy0xNTFiLTQxYTMtOWVjOS1jNjIwMTRkMzViNjEiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY5NDc3MzQ5NCwiZXhwIjoxNzI2MzA5NDk0fQ.NqR2FmgNgaaLRltlQfuKk4aEImfgEc8h5a3vHjvue3w'),
(3, 'cnto-bdlk-y4kt', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI3NGQwMDUwYy0xNTFiLTQxYTMtOWVjOS1jNjIwMTRkMzViNjEiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY5NDc3MzQ5NCwiZXhwIjoxNzI2MzA5NDk0fQ.NqR2FmgNgaaLRltlQfuKk4aEImfgEc8h5a3vHjvue3w');

-- --------------------------------------------------------

--
-- Table structure for table `sanpham`
--

CREATE TABLE `sanpham` (
  `id` int(11) NOT NULL,
  `tensp` varchar(250) NOT NULL,
  `giasp` varchar(100) NOT NULL,
  `hinhanh` text NOT NULL,
  `mota` text NOT NULL,
  `moi` int(1) DEFAULT 0,
  `giamgia` int(3) DEFAULT 0,
  `linkvideo` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `linksource` text DEFAULT NULL,
  `loai` int(2) NOT NULL DEFAULT 0,
  `sltonkho` int(3) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `sanpham`
--

INSERT INTO `sanpham` (`id`, `tensp`, `giasp`, `hinhanh`, `mota`, `moi`, `giamgia`, `linkvideo`, `linksource`, `loai`, `sltonkho`) VALUES
(1, 'Mỳ xào xá xíu Hong Kong', '75', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-xa-xiu-hong-kong.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ dai gi&ograve;n.</p>\r\n\r\n<p>+) X&aacute; x&iacute;u.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 10, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/mi-xao-xa-xiu-hong-kong', 1, 474),
(2, 'Miến xào hải sản', '100', 'http://192.168.1.63:8000/storage/uploads/sanpham/mieng-xao-hai-san.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi miến dai, gi&ograve;n.</p>\r\n\r\n<p>+) Hải sản: T&ocirc;m, mực.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 20, 'XxDTDCfXgEw', '', 1, 497),
(3, 'Hủ tiếu hải sản', '75', 'http://192.168.1.63:8000/storage/uploads/sanpham/hu-tieu-xao-hai-san.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi hủ tiếu dai, gi&ograve;n.</p>\r\n\r\n<p>+) T&ocirc;m x&uacute;.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 484),
(4, 'Bún thịt bò xào Nam Bộ', '75', 'http://192.168.1.63:8000/storage/uploads/sanpham/bun-thit-bo-xao-nam-bo.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi b&uacute;n to.</p>\r\n\r\n<p>+) Thịt thăn b&ograve;.</p>\r\n\r\n<p>+) Lạc rang.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 10, 'XxDTDCfXgEw', '', 1, 500),
(5, 'Nui xào bò Hàn Quốc', '80', 'http://192.168.1.63:8000/storage/uploads/sanpham/nui-xao-bo-han-quoc.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Nui dai, gi&ograve;n, sợi to.</p>\r\n\r\n<p>+) Thịt thăn b&ograve; H&agrave;n Quốc.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 3, 'XxDTDCfXgEw', '', 1, 200),
(6, 'Hủ tiếu xào bò', '75', 'http://192.168.1.63:8000/storage/uploads/sanpham/hu-tieu-xao-bo.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi hủ tiếu dai, gi&ograve;n.</p>\r\n\r\n<p>+) Thịt thăn b&ograve;.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 500),
(7, 'Mỳ Ý sốt cà bò', '65', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-y-sot-ca-bo.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Mỳ &Yacute;.</p>\r\n\r\n<p>+) Thịt b&ograve; băm nhuyễn.</p>\r\n\r\n<p>+) Sốt c&agrave; chua.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', '', 1, 500),
(8, 'Mỳ ý xào hải sản bốn mùa', '80', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-y-xao-hai-san-bon-mua.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ dai, gi&ograve;n.</p>\r\n\r\n<p>+) T&ocirc;m, mực, trứng chi&ecirc;n.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 10, 'XxDTDCfXgEw', '', 1, 500),
(9, 'Mỳ Ý xào bò bốn mùa', '75', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-y-xao-bo-bon-mua.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Mỳ &Yacute;.</p>\r\n\r\n<p>+) Thịt thăn b&ograve;, trứng chi&ecirc;n.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 300),
(10, 'Mỳ Ý hải sản', '79', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-y-hai-san.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Mỳ &Yacute;.</p>\r\n\r\n<p>+) Hải sản: H&eacute;n, t&ocirc;m x&uacute;, mực.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 500),
(11, 'Mỳ xào hải sản', '75', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-hai-san.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ dai, gi&ograve;n.</p>\r\n\r\n<p>+) Hải sản: T&ocirc;m, mực.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 500),
(12, 'Mỳ xào thập cẩm', '85', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-thap-cam.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ dai, gi&ograve;n.</p>\r\n\r\n<p>+) T&ocirc;m, thăn b&ograve;, mực.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 10, 'XxDTDCfXgEw', '', 1, 500),
(13, 'Mỳ xào giòn hải sản', '80', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-gion-hai-san.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ được chi&ecirc;n gi&ograve;n.</p>\r\n\r\n<p>+) T&ocirc;m, mực.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 10, 'XxDTDCfXgEw', '', 1, 500),
(14, 'Mỳ xào giòn thập cẩm', '85', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-gion-thap-cam.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ được chi&ecirc;n gi&ograve;n.</p>\r\n\r\n<p>+) Thịt thăn b&ograve;, t&ocirc;m.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 10, 'XxDTDCfXgEw', '', 1, 500),
(15, 'Mỳ xào giòn thịt bò', '79', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-gion-thit-bo.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ được chi&ecirc;n gi&ograve;n.</p>\r\n\r\n<p>+) Thịt thăn b&ograve;.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 500),
(16, 'Mỳ xào giòn chay', '60', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-gion-chay.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ được chi&ecirc;n gi&ograve;n.</p>\r\n\r\n<p>+) Rau, củ: S&uacute;p lơ,rau cải,...</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 500),
(17, 'Mỳ xào chay', '60', 'http://192.168.1.63:8000/storage/uploads/sanpham/mi-xao-chay.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi mỳ dai, gi&ograve;n.</p>\r\n\r\n<p>+) Rau, củ: S&uacute;p lơ, cải ngọt,...</p>\r\n\r\n<p>+) Nấm.</p>', 1, 5, 'XxDTDCfXgEw', '', 1, 500),
(18, 'Hủ tiếu xào chay', '60', 'http://192.168.1.63:8000/storage/uploads/sanpham/hu-tieu-xao-chay.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Sợi hủ tiếu to, dai.</p>\r\n\r\n<p>+) Rau, củ: S&uacute;p lơ, cải ngọt, c&agrave; chua,...</p>', 1, 10, 'XxDTDCfXgEw', '', 1, 500),
(19, 'Súp rau bina', '30', 'http://192.168.1.63:8000/storage/uploads/sanpham/sup-rau-bina.png', '- Nguyên liệu: Rau bina.', 1, 10, 'XxDTDCfXgEw', '', 2, 500),
(20, 'Súp đậu bắp', '35', 'http://192.168.1.63:8000/storage/uploads/sanpham/sup-dau-bap.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Đậu bắp.</p>\r\n\r\n<p>+) Bắp ngọt.</p>\r\n\r\n<p>+) C&agrave; rốt.</p>', 1, 5, 'XxDTDCfXgEw', '', 2, 500),
(21, 'Súp bông cải phô mai', '45', 'http://192.168.1.63:8000/storage/uploads/sanpham/sup-bong-cai-pho-mai-chay.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) B&ocirc;ng cải.</p>\r\n\r\n<p>+) Ph&ocirc; mai.</p>', 1, 5, 'XxDTDCfXgEw', '', 2, 500),
(22, 'Súp bí đỏ', '35', 'http://192.168.1.63:8000/storage/uploads/sanpham/sup-bi-do.png', '- Nguyên liệu: Bí đỏ.', 1, 5, 'XxDTDCfXgEw', '', 2, 500),
(23, 'Súp tỏi tây', '50', 'http://192.168.1.63:8000/storage/uploads/sanpham/sup-toi-tay.png', '- Nguyên liệu: Tỏi tây.', 1, 5, 'XxDTDCfXgEw', '', 2, 500),
(24, 'Cơm tay cầm gà xá xíu mật ong', '70', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-tay-cam-ga-xa-xiu-mat-ong.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) G&agrave; x&aacute; x&iacute;u.</p>\r\n\r\n<p>+) Trứng luộc.</p>\r\n\r\n<p>+) Mật ong.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 10, 'XxDTDCfXgEw', '', 3, 500),
(25, 'Cơm Teppan Gà Sốt Tiêu Đen', '65', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-teppan-ga-sot-tieu-den.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) Trứng ốp la.</p>\r\n\r\n<p>+) Thăn g&agrave;.</p>\r\n\r\n<p>+) Sốt ti&ecirc;u đen.</p>', 1, 0, 'XxDTDCfXgEw', '', 3, 497),
(26, 'Cơm Teppan hải sản', '89', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-teppan-hai-san.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) Trứng ốp la.</p>\r\n\r\n<p>+) Khoai t&acirc;y chi&ecirc;n.</p>\r\n\r\n<p>+) T&ocirc;m.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 3, 500),
(27, 'Cơm tay cầm thập cẩm', '79', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-tay-cam-thap-cam.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) Thịt b&ograve; miếng.</p>\r\n\r\n<p>+) T&ocirc;m.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 5, 'XxDTDCfXgEw', '', 3, 500),
(28, 'Cơm xanh Teppan bò lúc lắc', '69', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-xanh-teppan-bo-luc-lac.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) Bột tạo m&agrave;u xanh từ l&aacute; c&acirc;y.</p>\r\n\r\n<p>+) Trứng ốp la.</p>\r\n\r\n<p>+) Khoai t&acirc;y chi&ecirc;n.</p>\r\n\r\n<p>+) Thịt b&ograve; l&uacute;c lắc.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', '', 3, 700),
(29, 'Cơm Teppan Gà Sốt Cay', '69', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-teppan-ga-sot-cay.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) G&agrave; miếng.</p>\r\n\r\n<p>+) Trứng ốp la.</p>\r\n\r\n<p>+) Bắp ngọt.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>\r\n\r\n<p>+) Sốt cay.</p>', 1, 0, 'XxDTDCfXgEw', '', 3, 500),
(30, 'Cơm tay cầm hải sản', '75', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-tay-cam-hai-san.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) Hải sản: T&ocirc;m, ngao,...</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', '', 3, 500),
(31, 'Cơm chiên ba rọi kim chi', '69', 'http://192.168.1.63:8000/storage/uploads/sanpham/com-chien-ba-roi-kim-chi.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Cơm trắng.</p>\r\n\r\n<p>+) Thịt ba rọi.</p>\r\n\r\n<p>+) Kim chi.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', '', 3, 700),
(32, 'Lạp xưởng', '40', 'http://192.168.1.63:8000/storage/uploads/sanpham/lap-xuong-ct-5.png', '<p>- Nguy&ecirc;n liệu: Thịt lợn tươi.</p>\r\n\r\n<p>- Sản phẩm: 300g.</p>', 1, 5, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/lap-xuong', 4, 500),
(33, 'Xúc xích', '35', 'http://192.168.1.63:8000/storage/uploads/sanpham/xuc-xich.png', '<p>- Nguy&ecirc;n liệu: 100% thịt thăn lợn tươi.</p>\r\n\r\n<p>- Sản phẩm gồm 5 chiếc x&uacute;c x&iacute;ch.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/xuc-xich', 4, 500),
(34, 'Dăm bông xông khói', '350', 'http://192.168.1.63:8000/storage/uploads/sanpham/jambon.png', '<p>- Nguy&ecirc;n liệu: 100% thịt lợn, thịt đ&ugrave;i nguy&ecirc;n miếng.</p>\r\n\r\n<p>- Sản phẩm: 1kg.</p>', 1, 5, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/dam-bong-xong-khoi', 4, 500),
(35, 'Chả lụa bì', '280', 'http://192.168.1.63:8000/storage/uploads/sanpham/cha-lua-bi.png', '<p>- Nguy&ecirc;n liệu: 100% thịt lợn tươi.</p>\r\n\r\n<p>+) Thịt nạc: 50%.</p>\r\n\r\n<p>+) Thịt mỡ: 30%.</p>\r\n\r\n<p>+) B&igrave;: 20%.</p>\r\n\r\n<p>+) Ti&ecirc;u đen (C&oacute; thể c&oacute;).</p>\r\n\r\n<p>- Sản phẩm: 1kg.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/cha-lua-bi-1-can', 4, 400),
(36, 'Chả lụa bì cay', '280', 'http://192.168.1.63:8000/storage/uploads/sanpham/cha-lua-bi-cay.png', '<p>- Nguy&ecirc;n liệu: 100% thịt lợn tươi.</p>\r\n\r\n<p>+) Thịt nạc: 50%.</p>\r\n\r\n<p>+) Thịt mỡ: 30%.</p>\r\n\r\n<p>+) B&igrave;: 20%.</p>\r\n\r\n<p>+) Ti&ecirc;u đen, ướt.</p>\r\n\r\n<p>- Sản phẩm: 1kg.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/cha-lua-bi-cay-1-can', 4, 500),
(37, 'Giò lụa tai heo', '280', 'http://192.168.1.63:8000/storage/uploads/sanpham/gio-lua-tay-heo.png', '<p>- Nguy&ecirc;n liệu: 100% thịt lơn tươi.</p>\r\n\r\n<p>+) Thịt nạc: 60%.</p>\r\n\r\n<p>+) Thịt mỡ: 10%.</p>\r\n\r\n<p>+) Tai heo: 30%.</p>\r\n\r\n<p>- Sản phẩm: 1kg.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/gio-lua-tai-heo-1-can', 4, 500),
(38, 'Giò lụa', '250', 'http://192.168.1.63:8000/storage/uploads/sanpham/gio-lua-ct-1.png', '<p>- Nguy&ecirc;n liệu: 100% thịt lợn tươi.</p>\r\n\r\n<p>+) Thịt nạc: 90%.</p>\r\n\r\n<p>+) Thịt mỡ: 10%.</p>\r\n\r\n<p>- Sản phẩm: 1kg.</p>', 1, 10, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/gio-lua-1-can', 4, 500),
(39, 'Giò bò', '330', 'http://192.168.1.63:8000/storage/uploads/sanpham/gio-bo.png', '<p>- Nguy&ecirc;n liệu: 100% từ thịt b&ograve; tươi.</p>\r\n\r\n<p>+) Thịt nạc: 90%.</p>\r\n\r\n<p>+) Thịt mỡ: 10%.</p>\r\n\r\n<p>+) Ti&ecirc;u đen.</p>\r\n\r\n<p>- Sản phẩm: 1kg.</p>', 1, 5, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/gio-bo-1-can', 4, 500),
(40, 'Chả quế', '200', 'http://192.168.1.63:8000/storage/uploads/sanpham/cha-que-ct-3.png', '<p>- Nguy&ecirc;n liệu: 100% thịt lợn tươi.</p>\r\n\r\n<p>+) Thịt nạc: 50%.</p>\r\n\r\n<p>+) Thịt mỡ: 50%.</p>\r\n\r\n<p>+) Quế.</p>\r\n\r\n<p>- Sản phẩm: 1kg.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/cha-que-1-can', 4, 500),
(41, 'Bánh mỳ cá ngừ', '80', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-ca-ngu.png', '<p style=\"text-align:justify\">- Th&agrave;nh phần: B&aacute;nh m&igrave; n&acirc;u l&ecirc;n men l&ecirc;n men tự nhi&ecirc;n, c&aacute; ngừ, dầu &Ocirc;liu, c&agrave; rốt, dưa chuột, x&agrave; l&aacute;ch, m&ugrave;i t&acirc;y, sốt mayonese.</p>\r\n\r\n<p style=\"text-align:justify\">- B&aacute;nh m&igrave; C&aacute; ngừ vỏ n&acirc;u men tự nhi&ecirc;n với th&agrave;nh phần c&oacute; vỏ b&aacute;nh m&igrave; n&acirc;u men tự nhi&ecirc;n chứa nhiều chất xơ, tạo cảm gi&aacute;c no l&acirc;u v&agrave; hỗ trợ qu&aacute; tr&igrave;nh ti&ecirc;u h&oacute;a. B&ecirc;n trong, lớp c&aacute; ngừ tươi được chế biến kỹ c&agrave;ng, giữ nguy&ecirc;n hương vị thơm ngon v&agrave; gi&agrave;u chất đạm cần thiết cho cơ thể.</p>', 1, 5, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-ca-ngu', 5, 500),
(42, 'Bánh mỳ bò nướng phô mai', '35', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-bo-pho-mai.png', '<p style=\"text-align:justify\">- B&aacute;nh m&igrave; B&ograve; nướng pho-mai l&agrave; m&oacute;n b&aacute;nh m&igrave; rất được y&ecirc;u th&iacute;ch.</p>\r\n\r\n<p style=\"text-align:justify\">- Qua nhiều lần nghi&ecirc;n cứu v&agrave; thay đổi c&ocirc;ng thức, định lượng, đội ngũ đầu bếp của B&aacute;nh M&igrave; Phố nhận thấy sự h&ograve;a quyện giữa thịt b&ograve;, pho-mai, xốt mật ong l&aacute; thơm v&agrave; cho ra m&oacute;n B&aacute;nh m&igrave; B&ograve; nướng pho-mai.</p>\r\n\r\n<p style=\"text-align:justify\">- Thịt b&ograve; vi&ecirc;n được nướng trong l&ograve; c&oacute; nhiệt độ ổn định trong thời gian nhất định. Sau khi lấy ra, lập tức rải ngay một lớp pho-mai b&ecirc;n tr&ecirc;n, để nhiệt độ của ch&iacute;nh vi&ecirc;n b&ograve; l&agrave;m chảy pho-mai v&agrave; ngấm v&agrave;o thịt. Sau khi cho nh&acirc;n v&agrave;o b&aacute;nh, sẽ rải th&ecirc;m một l&aacute;t pho-mai b&ecirc;n tr&ecirc;n, cho b&aacute;nh v&agrave;o l&ograve; nướng để vỏ b&aacute;nh gi&ograve;n v&agrave; pho-mai tan chảy h&ograve;a quyện c&ugrave;ng xốt mật ong l&aacute; thơm độc đ&aacute;o.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-bo-nuong-pho-mai', 5, 499),
(43, 'Bánh mỳ bơ', '12', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-bo-phap.png', '<p>- Nguy&ecirc;n liệu: Bột mỳ, bơ.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-bo-phap', 5, 500),
(44, 'Bánh mỳ chuột', '3', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-chuot.png', '- Nguyên liệu: Bột mỳ.', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-chuot', 5, 500),
(45, 'Bánh mỳ chuột', '3', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-chuot.png', '- Nguyên liệu: Bột mỳ.', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-chuot', 5, 500),
(46, 'Bánh mỳ hoa cúc', '45', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-hoa-cuc.png', '- Nguyên liệu: Bột mỳ, hoa cúc, bơ.', 0, 10, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-hoa-cuc', 5, 500),
(47, 'Bánh mỳ đen', '10', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-den.png', '- Nguyên liệu: Bột mỳ, socola, vừng.', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-den', 5, 500),
(48, 'Bánh mỳ dừa', '15', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-dua.png', '- Nguyên liệu: Bột mỳ, bơ, dừa.', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-dua', 5, 500),
(49, 'Bánh mỳ kem', '10', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-kem.png', '- Nguyên liệu: Bột mỳ, kem.', 1, 2, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-kem', 5, 500),
(50, 'Bánh mỳ phô mai chảy', '30', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-pho-mai-chay.png', '- Nguyên liệu: Bột mỳ, phô mai.', 1, 20, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-pho-mai-chay', 5, 400),
(51, 'Donut socola', '12', 'http://192.168.1.63:8000/storage/uploads/sanpham/donut-socola.png', '- Nguyên liệu: Bột mỳ, socola, kem.', 1, 2, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/donut-socola', 5, 300),
(52, 'Bánh mỳ chả phô mai', '15', 'http://192.168.1.63:8000/storage/uploads/sanpham/cha-pho-mai.png', '- Nguyên liệu: Bột mỳ, chả, phô mai.', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/cha-pho-mai', 5, 500),
(53, 'Bánh mỳ chả ruốc', '15', 'http://192.168.1.63:8000/storage/uploads/sanpham/cha-ruoc.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) Chả, ruốc.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/cha-ruoc', 5, 500),
(54, 'Pizza mini', '15', 'http://192.168.1.63:8000/storage/uploads/sanpham/pizza-mini.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) Ng&ocirc; ngọt, ớt ngọt.</p>\r\n\r\n<p>+) X&uacute;c x&iacute;ch.</p>', 1, 2, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/pizza-mini', 5, 400),
(55, 'Bánh mỳ sừng bò nho', '18', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-sung-bo-nho.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) Bơ.</p>\r\n\r\n<p>+) Nho sấy.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-sung-bo-nho', 5, 500),
(56, 'Bánh mỳ xúc xích, phô mai', '25', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-xuc-xich-pho-mai.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) X&uacute;c x&iacute;ch.</p>\r\n\r\n<p>+) Ph&ocirc; mai.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-xuc-xich-pho-mai', 5, 500),
(57, 'Bánh mỳ thịt nướng tiêu thơm', '27', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-thit-nuong-tieu-thom.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) Thịt nướng.</p>\r\n\r\n<p>+) Xốt ti&ecirc;u.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-thit-nuong-tieu-thom', 5, 500),
(58, 'Bánh mỳ rau phô mai', '15', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-rau-pho-mai.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) Ph&ocirc; mai.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-rau-pho-mai', 5, 500),
(59, 'Bánh mỳ pate', '15', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-pate.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) Pate.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-pate', 5, 500),
(60, 'Bánh mỳ gà nướng', '25', 'http://192.168.1.63:8000/storage/uploads/sanpham/banh-my-ga-nuong.png', '<p>- Nguy&ecirc;n liệu:</p>\r\n\r\n<p>+) Bột mỳ.</p>\r\n\r\n<p>+) G&agrave; nướng.</p>\r\n\r\n<p>+) Xốt mật ong.</p>\r\n\r\n<p>+) Rau, củ ăn k&egrave;m.</p>', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/banh-my-ga-nuong', 5, 500),
(61, 'Nước trái cây SJORA xoài đào', '25', 'http://192.168.1.63:8000/storage/uploads/sanpham/nuoc-xoai-dao.png', 'Nước trái cây SJORA xoài đào', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/nuoc-trai-cay-sjora-xoai-dao', 6, 495),
(62, 'Trà chanh', '10', 'http://192.168.1.63:8000/storage/uploads/sanpham/tra-chanh.png', 'Trà chanh', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/tra-chanh', 6, 400),
(63, 'Nước khoáng Dasani', '10', 'http://192.168.1.63:8000/storage/uploads/sanpham/dasani-water.png', '<p>Nước kho&aacute;ng Dasani.</p>', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/nuoc-khoang-dasani', 6, 500),
(64, '7-up', '10', 'http://192.168.1.63:8000/storage/uploads/sanpham/7-up.png', '7-up', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/7-up', 6, 500),
(65, 'Coca Cola', '12', 'http://192.168.1.63:8000/storage/uploads/sanpham/coca-cola.png', 'Coca Cola', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/coca-cola', 6, 500),
(66, 'Nước cam Fanta', '12', 'http://192.168.1.63:8000/storage/uploads/sanpham/nuoc-cam-fanta.png', 'Nước cam Fanta', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/nuoc-cam-fanta', 6, 500),
(67, 'Milo', '20', 'http://192.168.1.63:8000/storage/uploads/sanpham/milo.png', 'Milo', 1, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/milo', 6, 600),
(68, 'Trà vải', '20', 'http://192.168.1.63:8000/storage/uploads/sanpham/tra-vai.png', 'Trà vải', 0, 0, 'XxDTDCfXgEw', 'https://binhsna.bsite.net/product/tra-vai', 6, 500);

-- --------------------------------------------------------

--
-- Table structure for table `theloai`
--

CREATE TABLE `theloai` (
  `id` int(11) NOT NULL,
  `tentheloai` varchar(100) NOT NULL,
  `hinhanh` text NOT NULL,
  `mota` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `theloai`
--

INSERT INTO `theloai` (`id`, `tentheloai`, `hinhanh`, `mota`) VALUES
(1, 'Mỳ-Phở', 'http://192.168.1.63:8000/storage/uploads/theloai/bun-thit-bo-xao-nam-bo.png', 'Mỳ-Phở là món ăn truyền thống của người dân Việt Nam.'),
(2, 'Soup', 'http://192.168.1.63:8000/storage/uploads/theloai/canh-ngheu.png', '- Món ăn dạng canh, thơm ngon.'),
(3, 'Cơm-Xôi', 'http://192.168.1.63:8000/storage/uploads/theloai/com-chien-tom-lon.png', 'Xôi là món ăn truyền thống của người dân Việt Nam.'),
(4, 'Thịt nguội', 'http://192.168.1.63:8000/storage/uploads/theloai/lap-xuong.png', 'Thịt nguội rất tiện lợi có thể ăn liền, tươi ngon.'),
(5, 'Bánh', 'http://192.168.1.63:8000/storage/uploads/theloai/pizza-mini.png', 'Bánh mỳ, bánh kem,... rất ngon và tiện lợi.'),
(6, 'Đồ uống', 'http://192.168.1.63:8000/storage/uploads/theloai/tra-vai.png', 'Giả khát, bù nước, tăng năng lượng.');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `email` varchar(250) NOT NULL,
  `pass` varchar(250) NOT NULL,
  `username` varchar(100) NOT NULL,
  `avatar` text DEFAULT NULL,
  `mobile` varchar(15) NOT NULL,
  `uid` text NOT NULL,
  `token` text NOT NULL,
  `status` int(2) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `email`, `pass`, `username`, `avatar`, `mobile`, `uid`, `token`, `status`) VALUES
(1, 'binhsna@gmail.com', '123456', 'Nguyễn Công Bình', 'images/avatar_1.jpg', '+84356334125', '8YUhL3NjvidxHLInDaa1rduTEI62', 'd42TiKe7SoiiGUFVHTKlFK:APA91bFKmKOGWoz6s_QG5m720XOKTIRp7XRVVIGp5kJy9rDRpTJIn0myfjvrnlgNm5W7eHv2FFQPfmckjiG58fG7_T55MH5c4ClXeZvXW47fs31JgRrFqBzbteIkoUuGryJkEhv56OeT', 1),
(2, 'binh19964@huce.edu.vn', '1234567', 'Nguyễn Công Bình', 'images/avatar_2.jpg', '+84971912776', 'o1RxP2ELfnT4S6VYumzwo8IbM0w1', 'f5Bp5eTRTYSsD3AWPG9jUf:APA91bHM09EGnJCcNBa6WRG4rKw03mRb6r-lhgfxW80_W8Hn6KCubTy5CCv_nrOEjo-17i8O2Vz16H5zyhLx8Sd4Hu6_4QlcEuUZxamr1JAYXx3zTJlNGqYwjTTTR9TGBWnBtgZhbcDt', 0);

-- --------------------------------------------------------

--
-- Table structure for table `yeuthich`
--

CREATE TABLE `yeuthich` (
  `sp_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `yeuthich`
--

INSERT INTO `yeuthich` (`sp_id`, `user_id`) VALUES
(1, 2),
(2, 2),
(3, 2),
(6, 2),
(7, 2),
(10, 2),
(11, 2),
(13, 2),
(25, 2),
(42, 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chitietdonhang`
--
ALTER TABLE `chitietdonhang`
  ADD PRIMARY KEY (`iddonhang`,`idsp`);

--
-- Indexes for table `donhang`
--
ALTER TABLE `donhang`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `hinhanh`
--
ALTER TABLE `hinhanh`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `khuyenmai`
--
ALTER TABLE `khuyenmai`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `meeting`
--
ALTER TABLE `meeting`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sanpham`
--
ALTER TABLE `sanpham`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `theloai`
--
ALTER TABLE `theloai`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `yeuthich`
--
ALTER TABLE `yeuthich`
  ADD PRIMARY KEY (`sp_id`,`user_id`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `donhang`
--
ALTER TABLE `donhang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT for table `hinhanh`
--
ALTER TABLE `hinhanh`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `khuyenmai`
--
ALTER TABLE `khuyenmai`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `meeting`
--
ALTER TABLE `meeting`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `sanpham`
--
ALTER TABLE `sanpham`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT for table `theloai`
--
ALTER TABLE `theloai`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=102;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
