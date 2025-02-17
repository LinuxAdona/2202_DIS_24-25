-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 17, 2025 at 09:52 AM
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
-- Database: `2202_dis`
--

DELIMITER $$
--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `get_available_rooms_by_branch` (`branchId` INT) RETURNS INT(11)  BEGIN
    DECLARE totalAvailableRooms INT DEFAULT 0;

    SELECT 
        SUM(ROUND(d.available / 4)) INTO totalAvailableRooms
    FROM 
        branches b 
    LEFT JOIN 
        doors d ON b.branch_id = d.branch_id
    WHERE 
        b.branch_id = branchId
    GROUP BY 
        b.branch_id;

    RETURN totalAvailableRooms;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `get_occupied_units_by_branch` (`branchId` INT) RETURNS INT(11)  BEGIN
    DECLARE totalOccupiedUnits INT DEFAULT 0;

    SELECT 
        ((COUNT(d.door_id) - 1) * 4) - SUM(d.available) INTO totalOccupiedUnits
    FROM 
        branches b 
    LEFT JOIN 
        doors d ON b.branch_id = d.branch_id
    WHERE 
        b.branch_id = branchId
    GROUP BY 
        b.branch_id;

    RETURN totalOccupiedUnits;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `get_overall_occupancy_rate` () RETURNS DECIMAL(5,2)  BEGIN
    DECLARE totalUnits INT DEFAULT 0;
    DECLARE occupiedUnits INT DEFAULT 0;
    DECLARE occupancyRate DECIMAL(5,2) DEFAULT 0;

    SELECT 
        SUM(totalUnits) AS totalUnits,
        SUM(occupiedUnits) AS occupiedUnits
    INTO totalUnits, occupiedUnits
    FROM (
        SELECT 
            ((COUNT(d.door_id) - 1) * 4) AS totalUnits, 
            (((COUNT(d.door_id) - 1) * 4) - SUM(d.available)) AS occupiedUnits
        FROM 
            branches b 
        LEFT JOIN 
            doors d ON b.branch_id = d.branch_id
        GROUP BY 
            b.branch_id
    ) AS branchOccupancy;

    IF totalUnits = 0 THEN
        RETURN 0;
    ELSE
        SET occupancyRate = (occupiedUnits * 100.0 / totalUnits);
        RETURN occupancyRate;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `get_total_occupied_units` () RETURNS INT(11)  BEGIN
    DECLARE totalOccupiedUnits INT DEFAULT 0;

    SELECT 
        SUM(occupiedUnits) INTO totalOccupiedUnits
    FROM (
        SELECT 
            ((COUNT(d.door_id) - 1) * 4) - SUM(d.available) AS occupiedUnits
        FROM 
            branches b 
        LEFT JOIN 
            doors d ON b.branch_id = d.branch_id
        GROUP BY 
            b.branch_id
    ) AS branchOccupancy;

    RETURN totalOccupiedUnits;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `address`
--

CREATE TABLE `address` (
  `address_id` int(11) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `municipality` varchar(100) DEFAULT NULL,
  `barangay` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `address`
--

INSERT INTO `address` (`address_id`, `city`, `municipality`, `barangay`) VALUES
(1, 'Batangas', 'Balayan', 'Malalay'),
(2, 'Batangas', 'Balayan', 'Tanggoy'),
(3, 'Batangas', 'Balayan', 'Sampaga'),
(4, 'Batangas', 'Tuy', 'Magahis'),
(5, 'Batangas', 'Nasugbu', 'Reparo'),
(6, 'Batangas', 'Balayan', 'Baclaran'),
(7, 'Batangas', 'Balayan', 'Barangay 1 (Poblacion)'),
(8, 'Batangas', 'Balayan', 'Barangay 2 (Poblacion)'),
(9, 'Batangas', 'Balayan', 'Barangay 3 (Poblacion)'),
(10, 'Batangas', 'Balayan', 'Barangay 4 (Poblacion)'),
(11, 'Batangas', 'Balayan', 'Barangay 5 (Poblacion)'),
(12, 'Batangas', 'Balayan', 'Barangay 6 (Poblacion)'),
(13, 'Batangas', 'Balayan', 'Barangay 7 (Poblacion)'),
(14, 'Batangas', 'Balayan', 'Barangay 8 (Poblacion)'),
(15, 'Batangas', 'Balayan', 'Barangay 9 (Poblacion)'),
(16, 'Batangas', 'Balayan', 'Barangay 10 (Poblacion)'),
(17, 'Batangas', 'Balayan', 'Barangay 11 (Poblacion)'),
(18, 'Batangas', 'Balayan', 'Barangay 12 (Poblacion)'),
(19, 'Batangas', 'Balayan', 'Calan'),
(20, 'Batangas', 'Balayan', 'Caloocan'),
(21, 'Batangas', 'Balayan', 'Calzada'),
(22, 'Batangas', 'Balayan', 'Canda'),
(23, 'Batangas', 'Balayan', 'Carenahan'),
(24, 'Batangas', 'Balayan', 'Caybunga'),
(25, 'Batangas', 'Balayan', 'Cayponce'),
(26, 'Batangas', 'Balayan', 'Dalig'),
(27, 'Batangas', 'Balayan', 'Dao'),
(28, 'Batangas', 'Balayan', 'Dilao'),
(29, 'Batangas', 'Balayan', 'Duhatan'),
(30, 'Batangas', 'Balayan', 'Durungao'),
(31, 'Batangas', 'Balayan', 'Gimalas'),
(32, 'Batangas', 'Balayan', 'Gumamela'),
(33, 'Batangas', 'Balayan', 'Lagnas'),
(34, 'Batangas', 'Balayan', 'Lanatan'),
(35, 'Batangas', 'Balayan', 'Langgangan'),
(36, 'Batangas', 'Balayan', 'Lucban Pook'),
(37, 'Batangas', 'Balayan', 'Lucban Putol'),
(38, 'Batangas', 'Balayan', 'Magabe'),
(39, 'Batangas', 'Balayan', 'Malalay'),
(40, 'Batangas', 'Balayan', 'Munting Tubig'),
(41, 'Batangas', 'Balayan', 'Navotas'),
(42, 'Batangas', 'Balayan', 'Palikpikan'),
(43, 'Batangas', 'Balayan', 'Patugo'),
(44, 'Batangas', 'Balayan', 'Pooc'),
(45, 'Batangas', 'Balayan', 'Sambat'),
(46, 'Batangas', 'Balayan', 'San Juan'),
(47, 'Batangas', 'Balayan', 'San Piro'),
(48, 'Batangas', 'Balayan', 'Santo Niño'),
(49, 'Batangas', 'Balayan', 'Sukol'),
(50, 'Batangas', 'Balayan', 'Tactac'),
(51, 'Batangas', 'Balayan', 'Tuyon-tuyon'),
(52, 'Batangas', 'Lian', 'Bagong Pook'),
(53, 'Batangas', 'Lian', 'Balibago'),
(54, 'Batangas', 'Lian', 'Barangay 1 (Poblacion)'),
(55, 'Batangas', 'Lian', 'Barangay 2 (Poblacion)'),
(56, 'Batangas', 'Lian', 'Barangay 3 (Poblacion)'),
(57, 'Batangas', 'Lian', 'Barangay 4 (Poblacion)'),
(58, 'Batangas', 'Lian', 'Barangay 5 (Poblacion)'),
(59, 'Batangas', 'Lian', 'Binubusan'),
(60, 'Batangas', 'Lian', 'Bungahan'),
(61, 'Batangas', 'Lian', 'Cumba'),
(62, 'Batangas', 'Lian', 'Humayingan'),
(63, 'Batangas', 'Lian', 'Kapito'),
(64, 'Batangas', 'Lian', 'Lumaniag'),
(65, 'Batangas', 'Lian', 'Luyahan'),
(66, 'Batangas', 'Lian', 'Malaruhatan'),
(67, 'Batangas', 'Lian', 'Matabungkay'),
(68, 'Batangas', 'Lian', 'Prenza'),
(69, 'Batangas', 'Lian', 'Puting-Kahoy'),
(70, 'Batangas', 'Lian', 'San Diego'),
(71, 'Batangas', 'Calatagan', 'Bagong Silang'),
(72, 'Batangas', 'Calatagan', 'Baha'),
(73, 'Batangas', 'Calatagan', 'Balibago'),
(74, 'Batangas', 'Calatagan', 'Balitoc'),
(75, 'Batangas', 'Calatagan', 'Barangay 1 (Poblacion)'),
(76, 'Batangas', 'Calatagan', 'Barangay 2 (Poblacion)'),
(77, 'Batangas', 'Calatagan', 'Barangay 3 (Poblacion)'),
(78, 'Batangas', 'Calatagan', 'Barangay 4 (Poblacion)'),
(79, 'Batangas', 'Calatagan', 'Biga'),
(80, 'Batangas', 'Calatagan', 'Bucal'),
(81, 'Batangas', 'Calatagan', 'Carlosa'),
(82, 'Batangas', 'Calatagan', 'Carretunan'),
(83, 'Batangas', 'Calatagan', 'Encarnacion'),
(84, 'Batangas', 'Calatagan', 'Gulod'),
(85, 'Batangas', 'Calatagan', 'Hukay'),
(86, 'Batangas', 'Calatagan', 'Lucsuhin'),
(87, 'Batangas', 'Calatagan', 'Luya'),
(88, 'Batangas', 'Calatagan', 'Paraiso'),
(89, 'Batangas', 'Calatagan', 'Quilitisan'),
(90, 'Batangas', 'Calatagan', 'Real'),
(91, 'Batangas', 'Calatagan', 'Sambungan'),
(92, 'Batangas', 'Calatagan', 'Santa Ana'),
(93, 'Batangas', 'Calatagan', 'Talibayog'),
(94, 'Batangas', 'Calatagan', 'Talisay'),
(95, 'Batangas', 'Calatagan', 'Tanagan'),
(96, 'Batangas', 'Tuy', 'Acle'),
(97, 'Batangas', 'Tuy', 'Bayudbud'),
(98, 'Batangas', 'Tuy', 'Bolboc (Maligas)'),
(99, 'Batangas', 'Tuy', 'Dalima'),
(100, 'Batangas', 'Tuy', 'Dao'),
(101, 'Batangas', 'Tuy', 'Guinhawa'),
(102, 'Batangas', 'Tuy', 'Lumbangan'),
(103, 'Batangas', 'Tuy', 'Luntal'),
(104, 'Batangas', 'Tuy', 'Magahis'),
(105, 'Batangas', 'Tuy', 'Malibu'),
(106, 'Batangas', 'Tuy', 'Mataywanac'),
(107, 'Batangas', 'Tuy', 'Palincaro'),
(108, 'Batangas', 'Tuy', 'Luna (Poblacion)'),
(109, 'Batangas', 'Tuy', 'Burgos (Poblacion)'),
(110, 'Batangas', 'Tuy', 'Rizal (Poblacion)'),
(111, 'Batangas', 'Tuy', 'Rillo (Poblacion)'),
(112, 'Batangas', 'Tuy', 'Putol'),
(113, 'Batangas', 'Tuy', 'Sabang'),
(114, 'Batangas', 'Tuy', 'San Jose'),
(115, 'Batangas', 'Tuy', 'San Jose (Putic)'),
(116, 'Batangas', 'Tuy', 'Talon'),
(117, 'Batangas', 'Tuy', 'Toong'),
(118, 'Batangas', 'Tuy', 'Tuyon-tuyon (Obispo)'),
(119, 'Batangas', 'Nasugbu', 'Aga'),
(120, 'Batangas', 'Nasugbu', 'Balaytigui'),
(121, 'Batangas', 'Nasugbu', 'Banilad'),
(122, 'Batangas', 'Nasugbu', 'Bilaran'),
(123, 'Batangas', 'Nasugbu', 'Bucana'),
(124, 'Batangas', 'Nasugbu', 'Bulihan'),
(125, 'Batangas', 'Nasugbu', 'Bunducan'),
(126, 'Batangas', 'Nasugbu', 'Butucan'),
(127, 'Batangas', 'Nasugbu', 'Calayo'),
(128, 'Batangas', 'Nasugbu', 'Catandaan'),
(129, 'Batangas', 'Nasugbu', 'Cogunan'),
(130, 'Batangas', 'Nasugbu', 'Dayap'),
(131, 'Batangas', 'Nasugbu', 'Gumamela'),
(132, 'Batangas', 'Nasugbu', 'Jolo'),
(133, 'Batangas', 'Nasugbu', 'Kaylaway'),
(134, 'Batangas', 'Nasugbu', 'Kayrilaw'),
(135, 'Batangas', 'Nasugbu', 'Latag'),
(136, 'Batangas', 'Nasugbu', 'Looc'),
(137, 'Batangas', 'Nasugbu', 'Lumbangan'),
(138, 'Batangas', 'Nasugbu', 'Malapad na Bato'),
(139, 'Batangas', 'Nasugbu', 'Maugat'),
(140, 'Batangas', 'Nasugbu', 'Munting Indan'),
(141, 'Batangas', 'Nasugbu', 'Munting Mapino'),
(142, 'Batangas', 'Nasugbu', 'Natipuan'),
(143, 'Batangas', 'Nasugbu', 'Pantalan'),
(144, 'Batangas', 'Nasugbu', 'Papaya'),
(145, 'Batangas', 'Nasugbu', 'Putat'),
(146, 'Batangas', 'Nasugbu', 'Reparo'),
(147, 'Batangas', 'Nasugbu', 'Tumalim'),
(148, 'Batangas', 'Nasugbu', 'Utod'),
(149, 'Batangas', 'Nasugbu', 'Wawa'),
(150, 'Batangas', 'Nasugbu', 'Barangay 1 (Poblacion)'),
(151, 'Batangas', 'Nasugbu', 'Barangay 2 (Poblacion)'),
(152, 'Batangas', 'Nasugbu', 'Barangay 3 (Poblacion)'),
(153, 'Batangas', 'Nasugbu', 'Barangay 4 (Poblacion)'),
(154, 'Batangas', 'Nasugbu', 'Barangay 5 (Poblacion)'),
(155, 'Batangas', 'Nasugbu', 'Barangay 6 (Poblacion)'),
(156, 'Batangas', 'Nasugbu', 'Barangay 7 (Poblacion)'),
(157, 'Batangas', 'Nasugbu', 'Barangay 8 (Poblacion)'),
(158, 'Batangas', 'Nasugbu', 'Barangay 9 (Poblacion)'),
(159, 'Batangas', 'Nasugbu', 'Barangay 10 (Poblacion)'),
(160, 'Batangas', 'Nasugbu', 'Barangay 11 (Poblacion)'),
(161, 'Batangas', 'Nasugbu', 'Barangay 12 (Poblacion)'),
(162, 'Batangas', 'Lian', 'Balibago'),
(163, 'Batangas', 'Calaca', 'Bagong Tubig'),
(164, 'Batangas', 'Calaca', 'Baclas'),
(165, 'Batangas', 'Calaca', 'Balimbing'),
(166, 'Batangas', 'Calaca', 'Bambang'),
(167, 'Batangas', 'Calaca', 'Barangay 1 (Poblacion)'),
(168, 'Batangas', 'Calaca', 'Barangay 2 (Poblacion)'),
(169, 'Batangas', 'Calaca', 'Barangay 3 (Poblacion)'),
(170, 'Batangas', 'Calaca', 'Barangay 4 (Poblacion)'),
(171, 'Batangas', 'Calaca', 'Barangay 5 (Poblacion)'),
(172, 'Batangas', 'Calaca', 'Barangay 6 (Poblacion)'),
(173, 'Batangas', 'Calaca', 'Bisaya'),
(174, 'Batangas', 'Calaca', 'Cahil'),
(175, 'Batangas', 'Calaca', 'Calantas'),
(176, 'Batangas', 'Calaca', 'Caluangan'),
(177, 'Batangas', 'Calaca', 'Camastilisan'),
(178, 'Batangas', 'Calaca', 'Coral ni Bacal'),
(179, 'Batangas', 'Calaca', 'Coral ni Lopez (Sugod)'),
(180, 'Batangas', 'Calaca', 'Dacanlao'),
(181, 'Batangas', 'Calaca', 'Dila'),
(182, 'Batangas', 'Calaca', 'Batulao'),
(183, 'Batangas', 'Calaca', 'Loma'),
(184, 'Batangas', 'Calaca', 'Lumbang Calzada'),
(185, 'Batangas', 'Calaca', 'Lumbang na Bata'),
(186, 'Batangas', 'Calaca', 'Lumbang na Matanda'),
(187, 'Batangas', 'Calaca', 'Madalunot'),
(188, 'Batangas', 'Calaca', 'Makina'),
(189, 'Batangas', 'Calaca', 'Matipok'),
(190, 'Batangas', 'Calaca', 'Munting Coral'),
(191, 'Batangas', 'Calaca', 'Niyugan'),
(192, 'Batangas', 'Calaca', 'Pantay'),
(193, 'Batangas', 'Calaca', 'Puting Bato West'),
(194, 'Batangas', 'Calaca', 'Puting Bato East'),
(195, 'Batangas', 'Calaca', 'Quisumbing'),
(196, 'Batangas', 'Calaca', 'Salong'),
(197, 'Batangas', 'Calaca', 'San Rafael'),
(198, 'Batangas', 'Calaca', 'Sinisian East'),
(199, 'Batangas', 'Calaca', 'Sinisian West'),
(200, 'Batangas', 'Calaca', 'Talisay'),
(201, 'Batangas', 'Calaca', 'Timbain'),
(202, 'Batangas', 'Calaca', 'Tiquiwan'),
(203, 'Batangas', 'Balayan', 'Santol');

-- --------------------------------------------------------

--
-- Stand-in structure for view `admin_branch`
-- (See below for the actual view)
--
CREATE TABLE `admin_branch` (
`user_id` int(11)
,`first_name` varchar(255)
,`last_name` varchar(255)
,`branch_id` int(11)
,`address_id` int(11)
);

-- --------------------------------------------------------

--
-- Table structure for table `billings`
--

CREATE TABLE `billings` (
  `billing_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `rent` decimal(10,2) DEFAULT NULL,
  `meter_type` enum('electric','water') DEFAULT NULL,
  `meter_bill` decimal(10,2) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `status` enum('paid','unpaid') DEFAULT 'unpaid'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `branches`
--

CREATE TABLE `branches` (
  `branch_id` int(11) NOT NULL,
  `address_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `created_at` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `branches`
--

INSERT INTO `branches` (`branch_id`, `address_id`, `user_id`, `created_at`) VALUES
(1, 21, 12, '2020-04-24'),
(2, 70, 13, '2021-08-10'),
(3, 94, 2, '2022-05-18'),
(4, 111, 10, '2023-11-09'),
(5, 137, 4, '2024-01-21');

-- --------------------------------------------------------

--
-- Table structure for table `doors`
--

CREATE TABLE `doors` (
  `door_id` int(11) NOT NULL,
  `branch_id` int(11) DEFAULT NULL,
  `door_number` int(11) DEFAULT NULL,
  `available` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `doors`
--

INSERT INTO `doors` (`door_id`, `branch_id`, `door_number`, `available`) VALUES
(1, 1, 101, 3),
(2, 1, 102, 4),
(3, 1, 103, 4),
(4, 1, 104, 4),
(5, 1, 105, 4),
(6, 1, 106, 4),
(7, 1, 107, 4),
(8, 1, 108, 4),
(9, 1, 109, 4),
(10, 1, 110, 4),
(11, 1, 111, 4),
(12, 1, 112, 4),
(13, 1, 113, 4),
(14, 1, 114, 4),
(15, 1, 115, 4),
(16, 2, 101, 4),
(17, 2, 102, 4),
(18, 2, 103, 4),
(19, 2, 104, 4),
(20, 2, 105, 4),
(21, 2, 106, 4),
(22, 2, 107, 4),
(23, 2, 108, 4),
(24, 2, 109, 4),
(25, 2, 110, 4),
(26, 2, 111, 4),
(27, 2, 112, 4),
(28, 2, 113, 4),
(29, 2, 114, 4),
(30, 2, 115, 4),
(31, 3, 101, 4),
(32, 3, 102, 4),
(33, 3, 103, 4),
(34, 3, 104, 4),
(35, 3, 105, 4),
(36, 3, 106, 4),
(37, 3, 107, 4),
(38, 3, 108, 4),
(39, 3, 109, 4),
(40, 3, 110, 4),
(41, 3, 111, 4),
(42, 3, 112, 4),
(43, 3, 113, 4),
(44, 3, 114, 4),
(45, 3, 115, 4),
(46, 4, 101, 4),
(47, 4, 102, 4),
(48, 4, 103, 4),
(49, 4, 104, 4),
(50, 4, 105, 4),
(51, 4, 106, 4),
(52, 4, 107, 4),
(53, 4, 108, 4),
(54, 4, 109, 4),
(55, 4, 110, 4),
(56, 4, 111, 4),
(57, 4, 112, 4),
(58, 4, 113, 4),
(59, 4, 114, 4),
(60, 4, 115, 4),
(61, 5, 101, 2),
(62, 5, 102, 0),
(63, 5, 103, 4),
(64, 5, 104, 4),
(65, 5, 105, 4),
(66, 5, 106, 4),
(67, 5, 107, 4),
(68, 5, 108, 4),
(69, 5, 109, 4),
(70, 5, 110, 4),
(71, 5, 111, 4),
(72, 5, 112, 4),
(73, 5, 113, 4),
(74, 5, 114, 4),
(75, 5, 115, 4),
(76, 1, 100, 0),
(77, 2, 100, 0),
(78, 3, 100, 0),
(79, 4, 100, 0),
(80, 5, 100, 0);

-- --------------------------------------------------------

--
-- Table structure for table `meters`
--

CREATE TABLE `meters` (
  `meter_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `meter_type` enum('electric','water') DEFAULT NULL,
  `meter_usage` double DEFAULT NULL,
  `reading_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notif_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `notif_date` datetime DEFAULT current_timestamp(),
  `status` enum('unread','read') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `amount_paid` double DEFAULT NULL,
  `date_paid` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `profiles`
--

CREATE TABLE `profiles` (
  `profile_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `door_id` int(11) DEFAULT NULL,
  `address_id` int(11) DEFAULT NULL,
  `profile_picture` longblob DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `contact_number` varchar(11) DEFAULT NULL,
  `sex` enum('Male','Female') DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `profiles`
--

INSERT INTO `profiles` (`profile_id`, `user_id`, `door_id`, `address_id`, `profile_picture`, `first_name`, `last_name`, `contact_number`, `sex`, `date_of_birth`) VALUES
(1, 1, NULL, 1, NULL, 'Linux', 'Adona', '09123456789', 'Male', '2004-07-17'),
(2, 2, 78, 2, NULL, 'John Dave', 'Briones', '09987654321', 'Male', '2005-09-09'),
(3, 3, 1, 126, NULL, 'Emmanuel', 'Mercado', '09987654322', 'Male', '1997-11-22'),
(4, 4, 80, 128, NULL, 'Kristine', 'De Torres', '09987654333', 'Female', '2004-09-03'),
(5, 5, 61, 3, NULL, 'Mac Millan', 'Abrenica', '09987654444', 'Male', '2004-09-18'),
(8, 10, 79, 108, NULL, 'Aeron Marc', 'Salanguit', '09987655555', 'Male', '2004-02-20'),
(9, 11, 61, 4, NULL, 'Dorina', 'Cables', '09987666666', 'Female', '2004-11-17'),
(10, 12, 76, 32, NULL, 'Jasper', 'Rosales', '09987777777', 'Male', '2004-04-26'),
(11, 13, 77, 52, NULL, 'Felman', 'Eleponga', '0988888888', 'Male', '2005-08-30'),
(12, 14, NULL, 31, NULL, 'Khriz Viyel', 'Ellao', '09123456789', 'Male', '2004-09-12'),
(13, 15, NULL, 67, NULL, 'Andre', 'Cachola', '09123456788', 'Male', '2005-05-13'),
(14, 16, NULL, 153, NULL, 'Axle', 'Hernando', '09123456777', 'Male', '2004-10-16'),
(15, 17, 62, 32, NULL, 'John Ashley', 'Alday', '09123456543', 'Male', '2004-12-22'),
(16, 18, 62, 203, NULL, 'Patrick Jay', 'Alday', '09123456666', 'Male', '2005-07-30'),
(17, 19, 62, 20, NULL, 'Ken', 'Gaa', '09123455555', 'Male', '2004-08-17'),
(18, 20, 62, 36, NULL, 'Katrina Ashley', 'Mayuga', '09123444444', 'Female', '2004-09-01');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('residents','admins','super') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `password`, `role`) VALUES
(1, 'linuxadona', 'linuxadona@gmail.com', 'linux123', 'super'),
(2, 'dabido', 'johndave@gmail.com', 'dave123', 'admins'),
(3, 'emman', 'emmanuel@gmail.com', 'emman123', 'residents'),
(4, 'tinay', 'kristinedetorres@gmail.com', 'tinay123', 'admins'),
(5, 'millan', 'millanabrenica@gmail.com', 'millan123', 'residents'),
(10, 'aeron', 'aeronsalanguit@gmail.com', 'aeron123', 'admins'),
(11, 'dorina', 'dorinacables@gmail.com', 'dorinaganda', 'residents'),
(12, 'jasper', 'jasperrosales@gmail.com', 'jasper123', 'admins'),
(13, 'felman', 'felmaneleponga@gmail.com', 'felman123', 'admins'),
(14, 'viyel', 'viyelellao@gmail.com', 'viyel123', 'residents'),
(15, 'andre', 'andrecachola@gmail.com', 'andre123', 'residents'),
(16, 'axle', 'axlehernando@gmail.com', 'axle123', 'residents'),
(17, 'busle', 'johnashleyalday@gmail.com', 'busle123', 'residents'),
(18, 'patek', 'patrickalday@gmail.com', 'patek123', 'residents'),
(19, 'bobby', 'kengaa@gmail.com', 'bobby123', 'residents'),
(20, 'katrina', 'katrinamayuga@gmail.com', 'kat123', 'residents');

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_account`
-- (See below for the actual view)
--
CREATE TABLE `user_account` (
`user_id` int(11)
,`username` varchar(20)
,`email` varchar(50)
,`password` varchar(255)
,`role` enum('residents','admins','super')
,`first_name` varchar(255)
,`last_name` varchar(255)
);

-- --------------------------------------------------------

--
-- Structure for view `admin_branch`
--
DROP TABLE IF EXISTS `admin_branch`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `admin_branch`  AS SELECT `u`.`user_id` AS `user_id`, `p`.`first_name` AS `first_name`, `p`.`last_name` AS `last_name`, `b`.`branch_id` AS `branch_id`, `a`.`address_id` AS `address_id` FROM (((`users` `u` join `profiles` `p` on(`u`.`user_id` = `p`.`user_id`)) join `branches` `b` on(`u`.`user_id` = `b`.`user_id`)) join `address` `a` on(`p`.`address_id` = `a`.`address_id`)) ;

-- --------------------------------------------------------

--
-- Structure for view `user_account`
--
DROP TABLE IF EXISTS `user_account`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `user_account`  AS SELECT `u`.`user_id` AS `user_id`, `u`.`username` AS `username`, `u`.`email` AS `email`, `u`.`password` AS `password`, `u`.`role` AS `role`, `p`.`first_name` AS `first_name`, `p`.`last_name` AS `last_name` FROM (`users` `u` left join `profiles` `p` on(`u`.`user_id` = `p`.`user_id`)) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `address`
--
ALTER TABLE `address`
  ADD PRIMARY KEY (`address_id`);

--
-- Indexes for table `billings`
--
ALTER TABLE `billings`
  ADD PRIMARY KEY (`billing_id`),
  ADD KEY `FK_USER_BILLINGS` (`user_id`);

--
-- Indexes for table `branches`
--
ALTER TABLE `branches`
  ADD PRIMARY KEY (`branch_id`),
  ADD KEY `FK_ADDRESS_BRANCH` (`address_id`),
  ADD KEY `FK_USER_BRANCHES` (`user_id`);

--
-- Indexes for table `doors`
--
ALTER TABLE `doors`
  ADD PRIMARY KEY (`door_id`),
  ADD KEY `FK_BRANCH` (`branch_id`);

--
-- Indexes for table `meters`
--
ALTER TABLE `meters`
  ADD PRIMARY KEY (`meter_id`),
  ADD KEY `FK_USER_METERS` (`user_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notif_id`),
  ADD KEY `FK_USER_NOTIFS` (`user_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `FK_USER_PAYMENTS` (`user_id`);

--
-- Indexes for table `profiles`
--
ALTER TABLE `profiles`
  ADD PRIMARY KEY (`profile_id`),
  ADD KEY `FK_DOOR_PROFILES` (`door_id`),
  ADD KEY `FK_ADDRESS_PROFILES` (`address_id`),
  ADD KEY `FK_USER_PROFILES` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `address`
--
ALTER TABLE `address`
  MODIFY `address_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=204;

--
-- AUTO_INCREMENT for table `billings`
--
ALTER TABLE `billings`
  MODIFY `billing_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `branches`
--
ALTER TABLE `branches`
  MODIFY `branch_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `doors`
--
ALTER TABLE `doors`
  MODIFY `door_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=81;

--
-- AUTO_INCREMENT for table `meters`
--
ALTER TABLE `meters`
  MODIFY `meter_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notif_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `profiles`
--
ALTER TABLE `profiles`
  MODIFY `profile_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `billings`
--
ALTER TABLE `billings`
  ADD CONSTRAINT `FK_USER_BILLINGS` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `branches`
--
ALTER TABLE `branches`
  ADD CONSTRAINT `FK_ADDRESS_BRANCH` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_USER_BRANCHES` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `doors`
--
ALTER TABLE `doors`
  ADD CONSTRAINT `FK_BRANCH` FOREIGN KEY (`branch_id`) REFERENCES `branches` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `meters`
--
ALTER TABLE `meters`
  ADD CONSTRAINT `FK_USER_METERS` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `FK_USER_NOTIFS` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `FK_USER_PAYMENTS` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `profiles`
--
ALTER TABLE `profiles`
  ADD CONSTRAINT `FK_ADDRESS_PROFILES` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_DOOR_PROFILES` FOREIGN KEY (`door_id`) REFERENCES `doors` (`door_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_USER_PROFILES` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
