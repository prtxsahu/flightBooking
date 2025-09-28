// Seed 100 Airport nodes for Neo4j
// Mix of US and international airports with coordinates

// US Major Airports
CREATE (jfk:Airport {code: "JFK", name: "John F. Kennedy International Airport", city: "New York", country: "USA", lat: 40.6413, lon: -73.7781});
CREATE (lax:Airport {code: "LAX", name: "Los Angeles International Airport", city: "Los Angeles", country: "USA", lat: 33.9425, lon: -118.4081});
CREATE (ord:Airport {code: "ORD", name: "O'Hare International Airport", city: "Chicago", country: "USA", lat: 41.9786, lon: -87.9048});
CREATE (dfw:Airport {code: "DFW", name: "Dallas/Fort Worth International Airport", city: "Dallas", country: "USA", lat: 32.8968, lon: -97.0380});
CREATE (atl:Airport {code: "ATL", name: "Hartsfield-Jackson Atlanta International Airport", city: "Atlanta", country: "USA", lat: 33.6407, lon: -84.4277});
CREATE (den:Airport {code: "DEN", name: "Denver International Airport", city: "Denver", country: "USA", lat: 39.8561, lon: -104.6737});
CREATE (sfo:Airport {code: "SFO", name: "San Francisco International Airport", city: "San Francisco", country: "USA", lat: 37.6213, lon: -122.3790});
CREATE (sea:Airport {code: "SEA", name: "Seattle-Tacoma International Airport", city: "Seattle", country: "USA", lat: 47.4502, lon: -122.3088});
CREATE (mia:Airport {code: "MIA", name: "Miami International Airport", city: "Miami", country: "USA", lat: 25.7959, lon: -80.2870});
CREATE (bwi:Airport {code: "BWI", name: "Baltimore/Washington International Thurgood Marshall Airport", city: "Baltimore", country: "USA", lat: 39.1774, lon: -76.6684});
CREATE (phx:Airport {code: "PHX", name: "Phoenix Sky Harbor International Airport", city: "Phoenix", country: "USA", lat: 33.4342, lon: -112.0116});
CREATE (las:Airport {code: "LAS", name: "Harry Reid International Airport", city: "Las Vegas", country: "USA", lat: 36.0840, lon: -115.1537});
CREATE (mco:Airport {code: "MCO", name: "Orlando International Airport", city: "Orlando", country: "USA", lat: 28.4312, lon: -81.3081});
CREATE (dtw:Airport {code: "DTW", name: "Detroit Metropolitan Wayne County Airport", city: "Detroit", country: "USA", lat: 42.2162, lon: -83.3554});
CREATE (msp:Airport {code: "MSP", name: "Minneapolis-Saint Paul International Airport", city: "Minneapolis", country: "USA", lat: 44.8848, lon: -93.2223});
CREATE (bna:Airport {code: "BNA", name: "Nashville International Airport", city: "Nashville", country: "USA", lat: 36.1245, lon: -86.6782});
CREATE (iad:Airport {code: "IAD", name: "Washington Dulles International Airport", city: "Washington", country: "USA", lat: 38.9531, lon: -77.4565});
CREATE (boston:Airport {code: "BOS", name: "Logan International Airport", city: "Boston", country: "USA", lat: 42.3656, lon: -71.0096});
CREATE (phl:Airport {code: "PHL", name: "Philadelphia International Airport", city: "Philadelphia", country: "USA", lat: 39.8729, lon: -75.2437});
CREATE (ewr:Airport {code: "EWR", name: "Newark Liberty International Airport", city: "Newark", country: "USA", lat: 40.6895, lon: -74.1745});

// US Regional Airports
CREATE (aus:Airport {code: "AUS", name: "Austin-Bergstrom International Airport", city: "Austin", country: "USA", lat: 30.1945, lon: -97.6699});
CREATE (pdx:Airport {code: "PDX", name: "Portland International Airport", city: "Portland", country: "USA", lat: 45.5898, lon: -122.5951});
CREATE (slc:Airport {code: "SLC", name: "Salt Lake City International Airport", city: "Salt Lake City", country: "USA", lat: 40.7899, lon: -111.9791});
CREATE (tpa:Airport {code: "TPA", name: "Tampa International Airport", city: "Tampa", country: "USA", lat: 27.9755, lon: -82.5332});
CREATE (rdu:Airport {code: "RDU", name: "Raleigh-Durham International Airport", city: "Raleigh", country: "USA", lat: 35.8776, lon: -78.7875});
CREATE (stl:Airport {code: "STL", name: "St. Louis Lambert International Airport", city: "St. Louis", country: "USA", lat: 38.7487, lon: -90.3700});
CREATE (cle:Airport {code: "CLE", name: "Cleveland Hopkins International Airport", city: "Cleveland", country: "USA", lat: 41.4117, lon: -81.8498});
CREATE (pit:Airport {code: "PIT", name: "Pittsburgh International Airport", city: "Pittsburgh", country: "USA", lat: 40.4915, lon: -80.2329});
CREATE (cvg:Airport {code: "CVG", name: "Cincinnati/Northern Kentucky International Airport", city: "Cincinnati", country: "USA", lat: 39.0488, lon: -84.6678});
CREATE (ind:Airport {code: "IND", name: "Indianapolis International Airport", city: "Indianapolis", country: "USA", lat: 39.7173, lon: -86.2944});

// International Airports - Europe
CREATE (lhr:Airport {code: "LHR", name: "London Heathrow Airport", city: "London", country: "UK", lat: 51.4700, lon: -0.4543});
CREATE (cdg:Airport {code: "CDG", name: "Charles de Gaulle Airport", city: "Paris", country: "France", lat: 49.0097, lon: 2.5479});
CREATE (fra:Airport {code: "FRA", name: "Frankfurt Airport", city: "Frankfurt", country: "Germany", lat: 50.0379, lon: 8.5622});
CREATE (ams:Airport {code: "AMS", name: "Amsterdam Airport Schiphol", city: "Amsterdam", country: "Netherlands", lat: 52.3105, lon: 4.7683});
CREATE (mad:Airport {code: "MAD", name: "Adolfo Suárez Madrid–Barajas Airport", city: "Madrid", country: "Spain", lat: 40.4983, lon: -3.5676});
CREATE (fco:Airport {code: "FCO", name: "Leonardo da Vinci–Fiumicino Airport", city: "Rome", country: "Italy", lat: 41.8003, lon: 12.2389});
CREATE (zur:Airport {code: "ZUR", name: "Zurich Airport", city: "Zurich", country: "Switzerland", lat: 47.4647, lon: 8.5492});
CREATE (vie:Airport {code: "VIE", name: "Vienna International Airport", city: "Vienna", country: "Austria", lat: 48.1103, lon: 16.5697});
CREATE (arn:Airport {code: "ARN", name: "Stockholm Arlanda Airport", city: "Stockholm", country: "Sweden", lat: 59.6519, lon: 17.9186});
CREATE (cph:Airport {code: "CPH", name: "Copenhagen Airport", city: "Copenhagen", country: "Denmark", lat: 55.6179, lon: 12.6561});

// International Airports - Asia
CREATE (nrt:Airport {code: "NRT", name: "Narita International Airport", city: "Tokyo", country: "Japan", lat: 35.7720, lon: 140.3928});
CREATE (icn:Airport {code: "ICN", name: "Incheon International Airport", city: "Seoul", country: "South Korea", lat: 37.4602, lon: 126.4407});
CREATE (pvg:Airport {code: "PVG", name: "Shanghai Pudong International Airport", city: "Shanghai", country: "China", lat: 31.1434, lon: 121.8052});
CREATE (hkg:Airport {code: "HKG", name: "Hong Kong International Airport", city: "Hong Kong", country: "Hong Kong", lat: 22.3080, lon: 113.9185});
CREATE (sin:Airport {code: "SIN", name: "Singapore Changi Airport", city: "Singapore", country: "Singapore", lat: 1.3644, lon: 103.9915});
CREATE (bkk:Airport {code: "BKK", name: "Suvarnabhumi Airport", city: "Bangkok", country: "Thailand", lat: 13.6900, lon: 100.7501});
CREATE (kul:Airport {code: "KUL", name: "Kuala Lumpur International Airport", city: "Kuala Lumpur", country: "Malaysia", lat: 2.7456, lon: 101.7099});
CREATE (mnl:Airport {code: "MNL", name: "Ninoy Aquino International Airport", city: "Manila", country: "Philippines", lat: 14.5086, lon: 121.0196});
CREATE (bom:Airport {code: "BOM", name: "Chhatrapati Shivaji Maharaj International Airport", city: "Mumbai", country: "India", lat: 19.0896, lon: 72.8656});
CREATE (del:Airport {code: "DEL", name: "Indira Gandhi International Airport", city: "New Delhi", country: "India", lat: 28.5562, lon: 77.1000});

// International Airports - Middle East & Africa
CREATE (dxb:Airport {code: "DXB", name: "Dubai International Airport", city: "Dubai", country: "UAE", lat: 25.2532, lon: 55.3657});
CREATE (doh:Airport {code: "DOH", name: "Hamad International Airport", city: "Doha", country: "Qatar", lat: 25.2611, lon: 51.5651});
CREATE (ist:Airport {code: "IST", name: "Istanbul Airport", city: "Istanbul", country: "Turkey", lat: 41.2753, lon: 28.7519});
CREATE (cai:Airport {code: "CAI", name: "Cairo International Airport", city: "Cairo", country: "Egypt", lat: 30.1127, lon: 31.4000});
CREATE (joh:Airport {code: "JNB", name: "O. R. Tambo International Airport", city: "Johannesburg", country: "South Africa", lat: -26.1367, lon: 28.2411});
CREATE (nbo:Airport {code: "NBO", name: "Jomo Kenyatta International Airport", city: "Nairobi", country: "Kenya", lat: -1.3192, lon: 36.9278});
CREATE (los:Airport {code: "LOS", name: "Murtala Muhammed International Airport", city: "Lagos", country: "Nigeria", lat: 6.5774, lon: 3.3212});
CREATE (cpt:Airport {code: "CPT", name: "Cape Town International Airport", city: "Cape Town", country: "South Africa", lat: -33.9648, lon: 18.6017});

// International Airports - Americas
CREATE (yyz:Airport {code: "YYZ", name: "Toronto Pearson International Airport", city: "Toronto", country: "Canada", lat: 43.6777, lon: -79.6248});
CREATE (yvr:Airport {code: "YVR", name: "Vancouver International Airport", city: "Vancouver", country: "Canada", lat: 49.1967, lon: -123.1815});
CREATE (gru:Airport {code: "GRU", name: "São Paulo/Guarulhos International Airport", city: "São Paulo", country: "Brazil", lat: -23.4356, lon: -46.4731});
CREATE (eze:Airport {code: "EZE", name: "Ministro Pistarini International Airport", city: "Buenos Aires", country: "Argentina", lat: -34.8222, lon: -58.5358});
CREATE (scl:Airport {code: "SCL", name: "Arturo Merino Benítez International Airport", city: "Santiago", country: "Chile", lat: -33.3928, lon: -70.7858});
CREATE (lim:Airport {code: "LIM", name: "Jorge Chávez International Airport", city: "Lima", country: "Peru", lat: -12.0219, lon: -77.1143});
CREATE (bog:Airport {code: "BOG", name: "El Dorado International Airport", city: "Bogotá", country: "Colombia", lat: 4.7016, lon: -74.1469});
CREATE (mex:Airport {code: "MEX", name: "Mexico City International Airport", city: "Mexico City", country: "Mexico", lat: 19.4363, lon: -99.0721});
CREATE (cun:Airport {code: "CUN", name: "Cancún International Airport", city: "Cancún", country: "Mexico", lat: 21.0365, lon: -86.8771});
CREATE (pty:Airport {code: "PTY", name: "Tocumen International Airport", city: "Panama City", country: "Panama", lat: 9.0714, lon: -79.3835});

// Additional US Airports
CREATE (san:Airport {code: "SAN", name: "San Diego International Airport", city: "San Diego", country: "USA", lat: 32.7338, lon: -117.1933});
CREATE (oak:Airport {code: "OAK", name: "Oakland International Airport", city: "Oakland", country: "USA", lat: 37.7213, lon: -122.2207});
CREATE (hou:Airport {code: "IAH", name: "George Bush Intercontinental Airport", city: "Houston", country: "USA", lat: 29.9844, lon: -95.3414});
CREATE (msy:Airport {code: "MSY", name: "Louis Armstrong New Orleans International Airport", city: "New Orleans", country: "USA", lat: 29.9934, lon: -90.2581});
CREATE (kci:Airport {code: "MCI", name: "Kansas City International Airport", city: "Kansas City", country: "USA", lat: 39.2976, lon: -94.7139});
CREATE (okc:Airport {code: "OKC", name: "Will Rogers World Airport", city: "Oklahoma City", country: "USA", lat: 35.3931, lon: -97.6007});
CREATE (mem:Airport {code: "MEM", name: "Memphis International Airport", city: "Memphis", country: "USA", lat: 35.0424, lon: -89.9767});
CREATE (ric:Airport {code: "RIC", name: "Richmond International Airport", city: "Richmond", country: "USA", lat: 37.5052, lon: -77.3197});
CREATE (buf:Airport {code: "BUF", name: "Buffalo Niagara International Airport", city: "Buffalo", country: "USA", lat: 42.9405, lon: -78.7322});
CREATE (roc:Airport {code: "ROC", name: "Greater Rochester International Airport", city: "Rochester", country: "USA", lat: 43.1189, lon: -77.6724});

// Additional International Airports
CREATE (hel:Airport {code: "HEL", name: "Helsinki Airport", city: "Helsinki", country: "Finland", lat: 60.3172, lon: 24.9633});
CREATE (osl:Airport {code: "OSL", name: "Oslo Airport", city: "Oslo", country: "Norway", lat: 60.1939, lon: 11.1004});
CREATE (bru:Airport {code: "BRU", name: "Brussels Airport", city: "Brussels", country: "Belgium", lat: 50.9014, lon: 4.4844});
CREATE (lis:Airport {code: "LIS", name: "Humberto Delgado Airport", city: "Lisbon", country: "Portugal", lat: 38.7813, lon: -9.1359});
CREATE (ath:Airport {code: "ATH", name: "Athens International Airport", city: "Athens", country: "Greece", lat: 37.9364, lon: 23.9445});
CREATE (prg:Airport {code: "PRG", name: "Václav Havel Airport Prague", city: "Prague", country: "Czech Republic", lat: 50.1008, lon: 14.2638});
CREATE (bud:Airport {code: "BUD", name: "Budapest Ferenc Liszt International Airport", city: "Budapest", country: "Hungary", lat: 47.4394, lon: 19.2618});
CREATE (waw:Airport {code: "WAW", name: "Warsaw Chopin Airport", city: "Warsaw", country: "Poland", lat: 52.1657, lon: 20.9671});
CREATE (kbp:Airport {code: "KBP", name: "Boryspil International Airport", city: "Kyiv", country: "Ukraine", lat: 50.3450, lon: 30.8947});
CREATE (svo:Airport {code: "SVO", name: "Sheremetyevo International Airport", city: "Moscow", country: "Russia", lat: 55.9736, lon: 37.4138});

// Additional Asian Airports
CREATE (kix:Airport {code: "KIX", name: "Kansai International Airport", city: "Osaka", country: "Japan", lat: 34.4342, lon: 135.2442});
CREATE (nkg:Airport {code: "NKG", name: "Nanjing Lukou International Airport", city: "Nanjing", country: "China", lat: 31.7420, lon: 118.8620});
CREATE (can:Airport {code: "CAN", name: "Guangzhou Baiyun International Airport", city: "Guangzhou", country: "China", lat: 23.3924, lon: 113.2988});
CREATE (tsa:Airport {code: "TSA", name: "Taipei Songshan Airport", city: "Taipei", country: "Taiwan", lat: 25.0697, lon: 121.5519});
CREATE (cgk:Airport {code: "CGK", name: "Soekarno–Hatta International Airport", city: "Jakarta", country: "Indonesia", lat: -6.1256, lon: 106.6558});
CREATE (dps:Airport {code: "DPS", name: "Ngurah Rai International Airport", city: "Denpasar", country: "Indonesia", lat: -8.7482, lon: 115.1672});
CREATE (hkt:Airport {code: "HKT", name: "Phuket International Airport", city: "Phuket", country: "Thailand", lat: 8.1132, lon: 98.3169});
CREATE (rep:Airport {code: "REP", name: "Siem Reap International Airport", city: "Siem Reap", country: "Cambodia", lat: 13.4107, lon: 103.8128});
CREATE (han:Airport {code: "HAN", name: "Noi Bai International Airport", city: "Hanoi", country: "Vietnam", lat: 21.2212, lon: 105.8072});
CREATE (sgn:Airport {code: "SGN", name: "Tan Son Nhat International Airport", city: "Ho Chi Minh City", country: "Vietnam", lat: 10.8188, lon: 106.6520});
