-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 08, 2026 at 02:03 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_bank_sampah`
--

-- --------------------------------------------------------

--
-- Table structure for table `tb_biodata`
--

CREATE TABLE `tb_biodata` (
  `id_biodata` int(11) NOT NULL,
  `id_nasabah` int(11) DEFAULT NULL,
  `nik` char(16) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `no_hp` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_biodata`
--

INSERT INTO `tb_biodata` (`id_biodata`, `id_nasabah`, `nik`, `nama_lengkap`, `alamat`, `no_hp`) VALUES
(1, 1, '3501010101010001', 'Dimas Andrean', 'Blitar', '081234560001'),
(2, 2, '3501010101010002', 'Andi Saputra', 'Malang', '081234560002'),
(3, 3, '3501010101010003', 'Budi Santoso', 'Surabaya', '081234560003'),
(4, 4, '3501010101010004', 'Sinta Maharani', 'Kediri', '081234560004'),
(5, 5, '3501010101010005', 'Rina Oktavia', 'Tulungagung', '081234560005'),
(6, 6, '3501010101010006', 'Fajar Nugroho', 'Jombang', '081234560006'),
(7, 7, '3501010101010007', 'Lina Kartika', 'Madiun', '081234560007'),
(8, 8, '3501010101010008', 'Eko Prasetyo', 'Nganjuk', '081234560008'),
(9, 9, '3501010101010009', 'Putri Lestari', 'Banyuwangi', '081234560009'),
(10, 10, '3501010101010010', 'Agus Setiawan', 'Lamongan', '081234560010'),
(11, 11, '0990878978909079', 'sdasdasfasfas', 'fasfafadga', '1123113'),
(12, 12, '3512345678901111', 'Budi', 'Malang', '081');

-- --------------------------------------------------------

--
-- Table structure for table `tb_nasabah`
--

CREATE TABLE `tb_nasabah` (
  `id_nasabah` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_nasabah`
--

INSERT INTO `tb_nasabah` (`id_nasabah`, `username`, `password`, `created_at`) VALUES
(1, 'dimas01', 'pass123', '2026-05-01 01:00:00'),
(2, 'andi02', 'pass123', '2026-05-02 01:00:00'),
(3, 'budi03', 'pass123', '2026-05-03 01:00:00'),
(4, 'sinta04', 'pass123', '2026-05-04 01:00:00'),
(5, 'rina05', 'pass123', '2026-05-05 01:00:00'),
(6, 'fajar06', 'pass123', '2026-05-06 01:00:00'),
(7, 'lina07', 'pass123', '2026-05-07 01:00:00'),
(8, 'eko08', 'pass123', '2026-05-08 01:00:00'),
(9, 'putri09', 'pass123', '2026-05-09 01:00:00'),
(10, 'agus10', 'pass123', '2026-05-10 01:00:00'),
(11, 'sdfss', 'dffasfasfa', '2026-06-02 15:09:36'),
(12, 'userA', 'pass1', '2026-06-02 15:30:04');

-- --------------------------------------------------------

--
-- Table structure for table `tb_status_kyc`
--

CREATE TABLE `tb_status_kyc` (
  `id_kyc` int(11) NOT NULL,
  `id_nasabah` int(11) DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `tanggal_verifikasi` datetime DEFAULT NULL,
  `diverifikasi_oleh` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_status_kyc`
--

INSERT INTO `tb_status_kyc` (`id_kyc`, `id_nasabah`, `status`, `tanggal_verifikasi`, `diverifikasi_oleh`) VALUES
(1, 1, 'APPROVED', '2026-05-11 09:00:00', 'Admin1'),
(2, 2, 'REJECTED', '2026-06-02 22:33:08', 'Admin_Tim_3'),
(3, 3, 'REJECTED', '2026-05-12 10:00:00', 'Admin2'),
(4, 4, 'APPROVED', '2026-05-13 11:00:00', 'Admin1'),
(5, 5, 'APPROVED', '2026-06-02 22:33:03', 'Admin_Tim_3'),
(6, 6, 'APPROVED', '2026-05-14 13:00:00', 'Admin3'),
(7, 7, 'REJECTED', '2026-05-15 14:00:00', 'Admin2'),
(8, 8, 'APPROVED', '2026-05-16 15:00:00', 'Admin1'),
(9, 9, 'APPROVED', '2026-06-02 22:33:13', 'Admin_Tim_3'),
(10, 10, 'APPROVED', '2026-05-17 16:00:00', 'Admin3'),
(11, 11, 'APPROVED', '2026-06-02 22:09:51', 'Admin_Tim_3'),
(12, 12, 'PENDING', NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tb_biodata`
--
ALTER TABLE `tb_biodata`
  ADD PRIMARY KEY (`id_biodata`),
  ADD UNIQUE KEY `nik` (`nik`),
  ADD KEY `id_nasabah` (`id_nasabah`);

--
-- Indexes for table `tb_nasabah`
--
ALTER TABLE `tb_nasabah`
  ADD PRIMARY KEY (`id_nasabah`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `tb_status_kyc`
--
ALTER TABLE `tb_status_kyc`
  ADD PRIMARY KEY (`id_kyc`),
  ADD KEY `id_nasabah` (`id_nasabah`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tb_biodata`
--
ALTER TABLE `tb_biodata`
  MODIFY `id_biodata` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `tb_nasabah`
--
ALTER TABLE `tb_nasabah`
  MODIFY `id_nasabah` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `tb_status_kyc`
--
ALTER TABLE `tb_status_kyc`
  MODIFY `id_kyc` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tb_biodata`
--
ALTER TABLE `tb_biodata`
  ADD CONSTRAINT `tb_biodata_ibfk_1` FOREIGN KEY (`id_nasabah`) REFERENCES `tb_nasabah` (`id_nasabah`) ON DELETE CASCADE;

--
-- Constraints for table `tb_status_kyc`
--
ALTER TABLE `tb_status_kyc`
  ADD CONSTRAINT `tb_status_kyc_ibfk_1` FOREIGN KEY (`id_nasabah`) REFERENCES `tb_nasabah` (`id_nasabah`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
