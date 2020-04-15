-- Create new customer
-- In UI, use dropdown for IATA_ID
INSERT INTO Customer (FirstName, LastName, Email, IATA_ID)
VALUES ('First', 'Last', 'ab@email.com', 'ORD');

-- Add new address for customer
-- In UI, use dropdown for country and state
INSERT INTO Address (StreetNumber, StreetName, City, State, ZipCode, Country)
VALUES (111, 'Street', 'City', 'IL', 60600, 'United States');
-- Do this if for customer address (not credit card) or to link address to owner
INSERT INTO LivesAt (Email, AddressID) VALUES ('ab@email.com', 1);

-- Modify address info
UPDATE Address
SET StreetNumber = 111, StreetName = 'Street', City = 'City', State = 'IL', ZipCode = 60600, Country = 'United States'
WHERE AddressID = 1;

-- Delete address
DELETE FROM Address
WHERE AddressID = 1;

-- Add credit card
INSERT INTO CreditCard (Type, CCNumber, CardFirstName, CardLastName, ExpirationDate, CVC, AddressID)
VALUES ('Visa', '1111000011110000', 'First', 'Last', 01-01-2000, '111', 1);
-- Link credit card to owner
INSERT INTO CreditCardOwner (Email, CCNumber)
VALUES ('ab@email.com', '1111000011110000');

-- Modify credit card (cannot modify ccnumber, if necessary create new cc)
UPDATE CreditCard
SET Type = 'Visa', CardFirstName = 'First', CardLastName = 'Last', ExpirationDate = 01-01-2000, CVC = '111', AddressID = 1
WHERE CCNumber = '1111000011110000';

-- Delete credit card
DELETE FROM CreditCard
WHERE CCNumber = '1111000011110000';



-- Search flights
-- Recursive query required
WITH RECURSIVE connections AS (
SELECT *, 1 path_length, ARRAY[airlineid, flightnumber]::text[] AS route FROM flight WHERE departureairport = 'ORD' AND (bookedcoach < maxcoach OR bookedfirst < maxfirstclass) AND date = '2017-08-12'
UNION
SELECT two.date, two.flightnumber, two.departureTime, two.maxcoach, two.maxfirstclass, two.arrivalTime, connections.departureairport, two.arrivalairport, two.airlineid,
    two.bookedcoach, two.bookedfirst, path_length + 1 AS path_length, route || ARRAY[two.airlineid, two.flightnumber]::text[] AS route
FROM connections, flight two
WHERE connections.arrivalairport = two.departureairport AND path_length < 3 AND two.date = '2017-08-12' AND (two.bookedcoach < two.maxcoach OR two.bookedfirst < two.maxfirstclass) AND (connections.arrivaltime - '00:30:00') > two.departuretime
)
SELECT route FROM connections WHERE arrivalairport = 'MIA';

-- Create booking
-- May need to add overbooking detection by decrementing MaxCoach or MaxFirstClass
-- Check will need to be added so MaxCoach and MaxFirstClass do not go below 0

-- Browse bookings for customer
-- In code, need to create a dictionary keyed on BookingID
SELECT b.FlightClass, bf.Date, bf.FlightNumber, bf.Airline, f.DepartureTime, f.ArrivalTime, f.DepartureAirport, f.ArrivalAirport
FROM Booking b JOIN BookingFlights bf ON b.BookingID = bf.BookingID
               JOIN Flight f ON bf.FlightNumber = f.FlightNumber
WHERE Email = 'ab@email.com';

-- Delete booking
-- If overbooking detection is added, increment MaxCoach and MaxFirstClass
DELETE FROM Booking
WHERE BookingID = 1;