package com.example.streetreport;

import java.util.ArrayList;

/**
 * Created by hoyoung on 2017-06-06.
 */

public class Api {

    public static final String GET_POST  = "http://taas.koroad.or.kr/data/rest/frequentzone/night?authKey=lT7w7Tm4vlSLGbuRapimxfZODOpR4VSQzSlca0N0g%2BuC%2B87TQBK9xNLopUN9QmTV&searchYearCd=2016148&sido=41&gugun=115&DEATH=N";

    public class Root{
        Reselt searchResult;
        int totalCount;
        String resultCode;

        public String getResultCode() {return resultCode;}
        public Reselt getSerchResult() {return searchResult;}
    }

    public class Reselt{
        ArrayList<Spot> frequentzone;
        public ArrayList<Spot> getFrequentzone() {return frequentzone;}
    }
    public class Spot{
        int fid;
        String grp_id;
        String occcd;
        String spotcd;
        String spot;
        String spotname;
        int occrrnc_co;
        int dthinj_co;
        int death_co;
        int serinj_co;
        int ordnr_co;
        int inj_co;
        String x_crd;
        String y_crd;

        public String getX_crd() {return x_crd;}
        public String getY_crd() {return y_crd;}
    }


//
//    "searchResult": {
//        "frequentzone": [
//    {
//        "fid": 6222910,
//            "grp_id": "2016148",
//            "occcd": "4111513400",
//            "spotcd": "41115002",
//            "spot": "경기도 수원시 팔달구2",
//            "spotname": "경기도 수원시 팔달구 매산로1가(수원역로터리 부근)",
//            "occrrnc_co": 34,
//            "dthinj_co": 42,
//            "death_co": 2,
//            "serinj_co": 6,
//            "ordnr_co": 31,
//            "inj_co": 3,
//            "geometry": "{ \"type\": \"Polygon\", \"coordinates\": [ [ [ 127.00324443657492, 37.266399484181889 ], [ 127.00323688498494, 37.26624232883492 ], [ 127.00321222477235, 37.266086297842193 ], [ 127.00317064335766, 37.265932578213537 ], [ 127.00311245796949, 37.265782339468792 ], [ 127.00303811004932, 37.265636726403784 ], [ 127.00294816646455, 37.265496845575477 ], [ 127.00284331162908, 37.265363762565805 ], [ 127.00272434420026, 37.265238489349549 ], [ 127.00259216808452, 37.265121979751115 ], [ 127.00244779023394, 37.265015121324204 ], [ 127.00229231056119, 37.264918725395773 ], [ 127.00212690953383, 37.264833527015448 ], [ 127.0019528493746, 37.264760174145479 ], [ 127.00177145152502, 37.264699224863143 ], [ 127.0015840977993, 37.264651143761455 ], [ 127.00139221463978, 37.264616295574299 ], [ 127.00119726183655, 37.264594946031231 ], [ 127.00100072241081, 37.264587257308698 ], [ 127.00080409246273, 37.26459328888901 ], [ 127.00060886879396, 37.264612993902887 ], [ 127.00041653535433, 37.264646222677669 ], [ 127.00022855874089, 37.264692721816672 ], [ 127.00004636674393, 37.264752138614902 ], [ 126.99987134722862, 37.264824020152091 ], [ 126.99970483230733, 37.264907819534542 ], [ 126.99954808816821, 37.265002899456334 ], [ 126.99940230942018, 37.26510853487833 ], [ 126.99926860323349, 37.265223923776198 ], [ 126.99914798934033, 37.265348187138912 ], [ 126.9990413841798, 37.265480378815376 ], [ 126.99894959971547, 37.265619493620441 ], [ 126.99887333438173, 37.265764471802065 ], [ 126.99881316848648, 37.26591421164008 ], [ 126.99876956080735, 37.266067572134823 ], [ 126.99874284285967, 37.266223386502745 ], [ 126.99873321884508, 37.266380469386746 ], [ 126.99874075995491, 37.266537624944036 ], [ 126.99876541106121, 37.2666936576911 ], [ 126.99880698390406, 37.266847378784718 ], [ 126.99886516264306, 37.266997618665279 ], [ 126.99893950379985, 37.267143235169797 ], [ 126.99902944185789, 37.267283118964833 ], [ 126.99913429256533, 37.267416205279673 ], [ 126.99925325739017, 37.2675414820391 ], [ 126.99938543250521, 37.267657995311112 ], [ 126.99952980986089, 37.267764857425554 ], [ 126.99968529290875, 37.267861256957588 ], [ 126.99985069660171, 37.26794645872922 ], [ 127.00002476311303, 37.268019814694036 ], [ 127.000206167465, 37.268080765765205 ], [ 127.00039352762808, 37.268128849972086 ], [ 127.00058542033123, 37.268163699823873 ], [ 127.00078038213921, 37.268185050427661 ], [ 127.00097693089106, 37.26819273867816 ], [ 127.00117357018551, 37.268186707784125 ], [ 127.00136880295464, 37.26816700191727 ], [ 127.00156114383833, 37.268133771672765 ], [ 127.00174912936841, 37.268087269604742 ], [ 127.00193132811692, 37.268027851169741 ], [ 127.00210635203113, 37.267955966661063 ], [ 127.00227287220825, 37.267872163978197 ], [ 127.00242961782351, 37.267777080508459 ], [ 127.0025753987001, 37.267671441417676 ], [ 127.00270910425387, 37.26755604882748 ], [ 127.00282971589968, 37.26743178186593 ], [ 127.00293631726682, 37.26729958679342 ], [ 127.00302809650684, 37.267160468898275 ], [ 127.00310435535016, 37.267015487125065 ], [ 127.00316451254051, 37.266865745975061 ], [ 127.00320811180386, 37.266712383820447 ], [ 127.0032348207527, 37.266556567490447 ], [ 127.00324443657492, 37.266399484181889 ] ] ] } ",
//            "x_crd": "127.000988827873",
//            "y_crd": "37.2663899980009"
//    }
// ]
//},
//        "totalCount": 4,
//        "resultCode": "Success"
//        }
}
