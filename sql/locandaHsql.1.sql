--
-- Table structure for table adjustment
--
 
DROP TABLE IF EXISTS adjustment;


CREATE TABLE adjustment (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  date DATE DEFAULT NULL,
  description VARCHAR(255) DEFAULT NULL,
  amount DOUBLE DEFAULT NULL,
  id_booking BIGINT DEFAULT NULL,
);


--
-- Table structure for table booker
--

DROP TABLE IF EXISTS booker;


CREATE TABLE booker (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_booking BIGINT DEFAULT NULL,
  id_guest BIGINT DEFAULT NULL,

);


--
-- Table structure for table booking
--

DROP TABLE IF EXISTS booking;


CREATE TABLE booking (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  dateIn DATE DEFAULT NULL,
  dateOut DATE DEFAULT NULL,
  nrGuests BIGINT DEFAULT NULL,
  roomSubtotal DOUBLE DEFAULT NULL,
  extraSubtotal DOUBLE DEFAULT NULL,
  status VARCHAR(255) DEFAULT NULL,
  notes VARCHAR(255) DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,
  id_convention BIGINT DEFAULT NULL,
  id_room BIGINT DEFAULT NULL,
);


--
-- Table structure for table convention
--

DROP TABLE IF EXISTS convention;


CREATE TABLE convention (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  description VARCHAR(255) DEFAULT NULL,
  activationCode VARCHAR(255) DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,
); 



--
-- Table structure for table extra
--

DROP TABLE IF EXISTS extra;


CREATE TABLE extra (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  timePriceType VARCHAR(255) DEFAULT NULL,
  resourcePriceType VARCHAR(255) DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,
  description VARCHAR(255) DEFAULT NULL,
  availableOnline TINYINT DEFAULT NULL,

);


--
-- Table structure for table extraItem
--

DROP TABLE IF EXISTS extraItem;


CREATE TABLE extraItem (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_booking BIGINT DEFAULT NULL,
  id_extra BIGINT DEFAULT NULL,
  quantity BIGINT DEFAULT NULL,
  maxQuantity BIGINT DEFAULT NULL,
  unitaryPrice DOUBLE DEFAULT NULL,

);


--
-- Table structure for table extraPriceList
--

DROP TABLE IF EXISTS extraPriceList;


CREATE TABLE extraPriceList (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_roomType BIGINT DEFAULT NULL,
  id_season BIGINT DEFAULT NULL,
  id_convention BIGINT DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,

);


--
-- Table structure for table extraPriceListItem
--

DROP TABLE IF EXISTS extraPriceListItem;


CREATE TABLE extraPriceListItem (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  price DOUBLE DEFAULT NULL,
  id_extra BIGINT DEFAULT NULL,
  id_extraPriceList BIGINT DEFAULT NULL,

);


--
-- Table structure for table facility
--

DROP TABLE IF EXISTS facility;


CREATE TABLE facility (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  description VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,

);


--
-- Table structure for table facilityImage
--

DROP TABLE IF EXISTS facilityImage;


CREATE TABLE facilityImage (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_facility BIGINT DEFAULT NULL,
  id_image BIGINT DEFAULT NULL,

);


--
-- Table structure for table file
--

DROP TABLE IF EXISTS file;


CREATE TABLE file (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  data BLOB,

);


--
-- Table structure for table guest
--

DROP TABLE IF EXISTS guest;


CREATE TABLE guest (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  firstName VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  lastName VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  email VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  phone varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  address VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  country VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  zipCode VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  notes VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  idNumber VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,
  gender VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  birthDate DATE DEFAULT NULL,
  birthPlace VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,

);


--
-- Table structure for table image
--

DROP TABLE IF EXISTS image;


CREATE TABLE image (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  caption VARCHAR(255) CHARACTER SET latin1 DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,

);


--
-- Table structure for table imageFile
--

DROP TABLE IF EXISTS imageFile;


CREATE TABLE imageFile (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_image BIGINT DEFAULT NULL,
  id_file BIGINT DEFAULT NULL,

);


--
-- Table structure for table payment
--

DROP TABLE IF EXISTS payment;


CREATE TABLE payment (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  date DATE DEFAULT NULL,
  description VARCHAR(255) DEFAULT NULL,
  amount DOUBLE DEFAULT NULL,
  id_booking BIGINT DEFAULT NULL,

);


--
-- Table structure for table room
--

DROP TABLE IF EXISTS room;


CREATE TABLE room (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  notes VARCHAR(255) DEFAULT NULL,
  id_roomType BIGINT DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,

);


--
-- Table structure for table roomFacility
--

DROP TABLE IF EXISTS roomFacility;


CREATE TABLE roomFacility (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_room BIGINT DEFAULT NULL,
  id_facility BIGINT DEFAULT NULL,

);


--
-- Table structure for table roomImage
--

DROP TABLE IF EXISTS roomImage;


CREATE TABLE roomImage (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_room BIGINT DEFAULT NULL,
  id_image BIGINT DEFAULT NULL,

);


--
-- Table structure for table roomPriceList
--

DROP TABLE IF EXISTS roomPriceList;


CREATE TABLE roomPriceList (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_roomType BIGINT DEFAULT NULL,
  id_season BIGINT DEFAULT NULL,
  id_convention BIGINT DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,

);


--
-- Table structure for table roomPriceListItem
--

DROP TABLE IF EXISTS roomPriceListItem;


CREATE TABLE roomPriceListItem (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  numGuests BIGINT DEFAULT NULL,
  priceSunday DOUBLE DEFAULT NULL,
  priceMonday DOUBLE DEFAULT NULL,
  priceTuesday DOUBLE DEFAULT NULL,
  priceWednesday DOUBLE DEFAULT NULL,
  priceThursday DOUBLE DEFAULT NULL,
  priceFriday DOUBLE DEFAULT NULL,
  priceSaturday DOUBLE DEFAULT NULL,
  id_roomPriceList BIGINT DEFAULT NULL,

);


--
-- Table structure for table roomType
--

DROP TABLE IF EXISTS roomType;


CREATE TABLE roomType (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  maxGuests BIGINT DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,
  notes VARCHAR(255) DEFAULT NULL,

);


--
-- Table structure for table roomTypeFacility
--

DROP TABLE IF EXISTS roomTypeFacility;


CREATE TABLE roomTypeFacility (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_roomType BIGINT DEFAULT NULL,
  id_facility BIGINT DEFAULT NULL,

);


--
-- Table structure for table roomTypeImage
--

DROP TABLE IF EXISTS roomTypeImage;


CREATE TABLE roomTypeImage (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_roomType BIGINT DEFAULT NULL,
  id_image BIGINT DEFAULT NULL,

);


--
-- Table structure for table season
--

DROP TABLE IF EXISTS season;


CREATE TABLE season (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  year BIGINT DEFAULT NULL,
  id_structure BIGINT DEFAULT NULL,

);


--
-- Table structure for table structure
--

DROP TABLE IF EXISTS structure;


CREATE TABLE structure (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  email VARCHAR(255) DEFAULT NULL,
  url VARCHAR(255) DEFAULT NULL,
  phone VARCHAR(255) DEFAULT NULL,
  fax VARCHAR(255) DEFAULT NULL,
  address VARCHAR(255) DEFAULT NULL,
  city VARCHAR(255) DEFAULT NULL,
  country VARCHAR(255) DEFAULT NULL,
  zipCode VARCHAR(255) DEFAULT NULL,
  notes VARCHAR(255) DEFAULT NULL,
  id_user BIGINT DEFAULT NULL,

);


--
-- Table structure for table structureFacility
--

DROP TABLE IF EXISTS structureFacility;


CREATE TABLE structureFacility (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_structure BIGINT DEFAULT NULL,
  id_facility BIGINT DEFAULT NULL,

);


--
-- Table structure for table structureImage
--

DROP TABLE IF EXISTS structureImage;


CREATE TABLE structureImage (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  id_structure BIGINT DEFAULT NULL,
  id_image BIGINT DEFAULT NULL,

);


--
-- Table structure for table user
--

DROP TABLE IF EXISTS user;


CREATE TABLE user (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) DEFAULT NULL,
  surname VARCHAR(255) DEFAULT NULL,
  email VARCHAR(255) DEFAULT NULL,
  phone VARCHAR(255) DEFAULT NULL,
  password VARCHAR(255) DEFAULT NULL,
  creationDate DATE DEFAULT NULL,
);



