        var map;
        var marker;
        function initMap() {
            var styleOptions = {
                name: "Dummy Style"
            };
            map = new google.maps.Map(document.getElementById('map'), {
                center: { lat: 22.785026, lng: 86.198168 },
                zoom: 14.5
            });
            var myStyle = [
                {
                    featureType: "administrative", elementType: "labels",
                    stylers: [{ visibility: "off" }]
                },
                {
                    featureType: "poi",
                    elementType: "labels",
                    stylers: [{ visibility: "off" }]
                }, {
                    featureType: "water",
                    elementType: "labels",
                    stylers: [{ visibility: "off" }]
                }, {
                    featureType: "road", elementType: "labels",
                    stylers: [
                        { visibility: "off" }
                    ]
                },
                {
                    featureType: "airport", elementType: "labels",
                    stylers: [
                        { visibility: "off" }
                    ]
                }
            ];
            
            // marker.setMap(map);
            var mapType = new google.maps.StyledMapType(myStyle, styleOptions);
            map.mapTypes.set("Dummy Style", mapType);
            
            

            

            map.setMapTypeId("Dummy Style");
            var triangleCoords = [
                new google.maps.LatLng(22.79180245, 86.18749738),
                new google.maps.LatLng(22.79115952, 86.18739009),
                new google.maps.LatLng(22.79097159, 86.18711114),
                new google.maps.LatLng(22.79093202, 86.1864996),
                new google.maps.LatLng(22.79100126, 86.1843431),
                new google.maps.LatLng(22.78838008, 86.18436456),
                new google.maps.LatLng(22.78839986, 86.18559837),
                new google.maps.LatLng(22.78729202, 86.18487954),
                new google.maps.LatLng(22.78588743, 86.18474007),
                new google.maps.LatLng(22.78413662, 86.18452549),
                new google.maps.LatLng(22.78268253, 86.18513703),
                new google.maps.LatLng(22.77958637, 86.1866498),
                new google.maps.LatLng(22.77907199, 86.18673563),
                new google.maps.LatLng(22.77866641, 86.18671417),
                new google.maps.LatLng(22.77841911, 86.18668199),
                new google.maps.LatLng(22.77822127, 86.18708968),
                new google.maps.LatLng(22.77782559, 86.18727207),
                new google.maps.LatLng(22.77701443, 86.1883235),
                new google.maps.LatLng(22.77673745, 86.18800163),
                new google.maps.LatLng(22.77507555, 86.19102716),
                new google.maps.LatLng(22.77607467, 86.19207859),
                new google.maps.LatLng(22.77421491, 86.19329095),
                new google.maps.LatLng(22.77370051, 86.19304419),
                new google.maps.LatLng(22.7723947, 86.1946857),
                new google.maps.LatLng(22.76949616, 86.19918108),
                new google.maps.LatLng(22.77115813, 86.20044708),
                new google.maps.LatLng(22.77306739, 86.19797945),
                new google.maps.LatLng(22.77424459, 86.19876266),
                new google.maps.LatLng(22.77497662, 86.20192766),
                new google.maps.LatLng(22.77709357, 86.20360136),
                new google.maps.LatLng(22.77865652, 86.20440602),
                new google.maps.LatLng(22.7789335, 86.20589733),
                new google.maps.LatLng(22.77784537, 86.20916963),
                new google.maps.LatLng(22.77777613, 86.20980263),
                new google.maps.LatLng(22.7778058, 86.2116158),
                new google.maps.LatLng(22.77785526, 86.21174455),
                new google.maps.LatLng(22.77952702, 86.21148705),
                new google.maps.LatLng(22.78087233, 86.21127248),
                new google.maps.LatLng(22.78134714, 86.21148705),
                new google.maps.LatLng(22.78157465, 86.21190548),
                new google.maps.LatLng(22.78196043, 86.21220589),
                new google.maps.LatLng(22.78267264, 86.21230245),
                new google.maps.LatLng(22.78305842, 86.21278524),
                new google.maps.LatLng(22.78270232, 86.21306419),
                new google.maps.LatLng(22.78321669, 86.21366501),
                new google.maps.LatLng(22.78376073, 86.21427655),
                new google.maps.LatLng(22.7840377, 86.2144804),
                new google.maps.LatLng(22.78461142, 86.2145555),
                new google.maps.LatLng(22.78616439, 86.21454477),
                new google.maps.LatLng(22.78731181, 86.21454477),
                new google.maps.LatLng(22.78844932, 86.21451259),
                new google.maps.LatLng(22.78958682, 86.21408343),
                new google.maps.LatLng(22.79045725, 86.21363282),
                new google.maps.LatLng(22.79275199, 86.21271014),
                new google.maps.LatLng(22.79377076, 86.21237755),
                new google.maps.LatLng(22.79576872, 86.2116158),
                new google.maps.LatLng(22.79543244, 86.21037126),
                new google.maps.LatLng(22.7981425, 86.20975971),
                new google.maps.LatLng(22.79828097, 86.20876193),
                new google.maps.LatLng(22.79849856, 86.20842934),
                new google.maps.LatLng(22.79827108, 86.20767832),
                new google.maps.LatLng(22.79817217, 86.20723844),
                new google.maps.LatLng(22.79913156, 86.20701313),
                new google.maps.LatLng(22.7996162, 86.20643377),
                new google.maps.LatLng(22.80017996, 86.2053287),
                new google.maps.LatLng(22.80009095, 86.20399833),
                new google.maps.LatLng(22.80011073, 86.2007153),
                new google.maps.LatLng(22.79998215, 86.19919181),
                new google.maps.LatLng(22.7995074, 86.19914889),
                new google.maps.LatLng(22.7995074, 86.19892359),
                new google.maps.LatLng(22.79986346, 86.19892359),
                new google.maps.LatLng(22.79981401, 86.19851589),
                new google.maps.LatLng(22.79930959, 86.19535089),
                new google.maps.LatLng(22.79923047, 86.19481444),
                new google.maps.LatLng(22.79914145, 86.19135976),
                new google.maps.LatLng(22.7992008, 86.1896646),
                new google.maps.LatLng(22.79917113, 86.18565202),
                new google.maps.LatLng(22.79827108, 86.18563056),
                new google.maps.LatLng(22.79733147, 86.18504047),
                new google.maps.LatLng(22.79517527, 86.1850512),
                new google.maps.LatLng(22.79453237, 86.18509412),
                new google.maps.LatLng(22.79401804, 86.18572712),
                new google.maps.LatLng(22.79391913, 86.18626356),
                new google.maps.LatLng(22.7932861, 86.18739009),
                new google.maps.LatLng(22.7926333, 86.18752956)

            ];

            var plotpoints = new google.maps.Polygon({
                paths: triangleCoords,
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillOpacity: 0,
                strokeColor: "#000"
            });
            plotpoints.setMap(map);



}
