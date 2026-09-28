package com.cantonadong.timezone;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/** Shared, offline city catalogue. Offsets are never hardcoded. */
public final class City {
    public final String id, zh, en, countryZh, countryEn, zone;
    public final int photo;
    private static final List<City> ALL = new ArrayList<>();
    private City(String id, String zh, String en, String cz, String ce, String zone, int photo) {
        this.id=id; this.zh=zh; this.en=en; countryZh=cz; countryEn=ce; this.zone=zone; this.photo=photo;
    }
    private static void add(String id, String zh, String en, String cz, String ce, String zone, int photo) {
        ALL.add(new City(id,zh,en,cz,ce,zone,photo));
    }
    private static void group(String countryZh,String countryEn,String zone,String... cities) {
        for(String entry:cities){String[] parts=entry.split("\\|",3);add(parts[0],parts[1],parts[2],countryZh,countryEn,zone,-1);}
    }
    static {
        add("beijing","北京","Beijing","中国","China","Asia/Shanghai",0);
        add("shanghai","上海","Shanghai","中国","China","Asia/Shanghai",-1);
        add("guangzhou","广州","Guangzhou","中国","China","Asia/Shanghai",-1);
        add("shenzhen","深圳","Shenzhen","中国","China","Asia/Shanghai",-1);
        add("chengdu","成都","Chengdu","中国","China","Asia/Shanghai",-1);
        add("chongqing","重庆","Chongqing","中国","China","Asia/Shanghai",-1);
        add("hongkong","香港","Hong Kong","中国","China","Asia/Hong_Kong",-1);
        add("macau","澳门","Macau","中国","China","Asia/Macau",-1);
        add("taipei","台北","Taipei","中国","China","Asia/Taipei",-1);
        add("tokyo","东京","Tokyo","日本","Japan","Asia/Tokyo",3);
        add("osaka","大阪","Osaka","日本","Japan","Asia/Tokyo",-1);
        add("seoul","首尔","Seoul","韩国","South Korea","Asia/Seoul",-1);
        add("singapore","新加坡","Singapore","新加坡","Singapore","Asia/Singapore",5);
        add("bangkok","曼谷","Bangkok","泰国","Thailand","Asia/Bangkok",-1);
        add("hanoi","河内","Hanoi","越南","Vietnam","Asia/Ho_Chi_Minh",-1);
        add("hochiminh","胡志明市","Ho Chi Minh City","越南","Vietnam","Asia/Ho_Chi_Minh",-1);
        add("kualalumpur","吉隆坡","Kuala Lumpur","马来西亚","Malaysia","Asia/Kuala_Lumpur",-1);
        add("jakarta","雅加达","Jakarta","印度尼西亚","Indonesia","Asia/Jakarta",-1);
        add("bali","巴厘岛","Bali","印度尼西亚","Indonesia","Asia/Makassar",-1);
        add("manila","马尼拉","Manila","菲律宾","Philippines","Asia/Manila",-1);
        add("delhi","新德里","New Delhi","印度","India","Asia/Kolkata",-1);
        add("mumbai","孟买","Mumbai","印度","India","Asia/Kolkata",-1);
        add("kathmandu","加德满都","Kathmandu","尼泊尔","Nepal","Asia/Kathmandu",-1);
        add("dhaka","达卡","Dhaka","孟加拉国","Bangladesh","Asia/Dhaka",-1);
        add("colombo","科伦坡","Colombo","斯里兰卡","Sri Lanka","Asia/Colombo",-1);
        add("karachi","卡拉奇","Karachi","巴基斯坦","Pakistan","Asia/Karachi",-1);
        add("dubai","迪拜","Dubai","阿联酋","United Arab Emirates","Asia/Dubai",-1);
        add("riyadh","利雅得","Riyadh","沙特阿拉伯","Saudi Arabia","Asia/Riyadh",-1);
        add("doha","多哈","Doha","卡塔尔","Qatar","Asia/Qatar",-1);
        add("tehran","德黑兰","Tehran","伊朗","Iran","Asia/Tehran",-1);
        add("jerusalem","耶路撒冷","Jerusalem","以色列","Israel","Asia/Jerusalem",-1);
        add("sydney","悉尼","Sydney","澳大利亚","Australia","Australia/Sydney",4);
        add("melbourne","墨尔本","Melbourne","澳大利亚","Australia","Australia/Melbourne",-1);
        add("brisbane","布里斯班","Brisbane","澳大利亚","Australia","Australia/Brisbane",-1);
        add("perth","珀斯","Perth","澳大利亚","Australia","Australia/Perth",-1);
        add("adelaide","阿德莱德","Adelaide","澳大利亚","Australia","Australia/Adelaide",-1);
        add("darwin","达尔文","Darwin","澳大利亚","Australia","Australia/Darwin",-1);
        add("auckland","奥克兰","Auckland","新西兰","New Zealand","Pacific/Auckland",-1);
        add("wellington","惠灵顿","Wellington","新西兰","New Zealand","Pacific/Auckland",-1);
        add("christchurch","基督城","Christchurch","新西兰","New Zealand","Pacific/Auckland",-1);
        add("chatham","查塔姆群岛","Chatham Islands","新西兰","New Zealand","Pacific/Chatham",-1);
        add("suva","苏瓦","Suva","斐济","Fiji","Pacific/Fiji",-1);
        add("honolulu","檀香山","Honolulu","美国","United States","Pacific/Honolulu",-1);
        add("newyork","纽约","New York","美国","United States","America/New_York",1);
        add("losangeles","洛杉矶","Los Angeles","美国","United States","America/Los_Angeles",-1);
        add("sanfrancisco","旧金山","San Francisco","美国","United States","America/Los_Angeles",-1);
        add("seattle","西雅图","Seattle","美国","United States","America/Los_Angeles",-1);
        add("chicago","芝加哥","Chicago","美国","United States","America/Chicago",-1);
        add("denver","丹佛","Denver","美国","United States","America/Denver",-1);
        add("phoenix","凤凰城","Phoenix","美国","United States","America/Phoenix",-1);
        add("anchorage","安克雷奇","Anchorage","美国","United States","America/Anchorage",-1);
        add("boston","波士顿","Boston","美国","United States","America/New_York",-1);
        add("washington","华盛顿","Washington, DC","美国","United States","America/New_York",-1);
        add("toronto","多伦多","Toronto","加拿大","Canada","America/Toronto",-1);
        add("vancouver","温哥华","Vancouver","加拿大","Canada","America/Vancouver",-1);
        add("stjohns","圣约翰斯","St. John's","加拿大","Canada","America/St_Johns",-1);
        add("mexicocity","墨西哥城","Mexico City","墨西哥","Mexico","America/Mexico_City",-1);
        add("saopaulo","圣保罗","São Paulo","巴西","Brazil","America/Sao_Paulo",-1);
        add("buenosaires","布宜诺斯艾利斯","Buenos Aires","阿根廷","Argentina","America/Argentina/Buenos_Aires",-1);
        add("santiago","圣地亚哥","Santiago","智利","Chile","America/Santiago",-1);
        add("lima","利马","Lima","秘鲁","Peru","America/Lima",-1);
        add("bogota","波哥大","Bogotá","哥伦比亚","Colombia","America/Bogota",-1);
        add("london","伦敦","London","英国","United Kingdom","Europe/London",2);
        add("paris","巴黎","Paris","法国","France","Europe/Paris",-1);
        add("berlin","柏林","Berlin","德国","Germany","Europe/Berlin",-1);
        add("rome","罗马","Rome","意大利","Italy","Europe/Rome",-1);
        add("madrid","马德里","Madrid","西班牙","Spain","Europe/Madrid",-1);
        add("lisbon","里斯本","Lisbon","葡萄牙","Portugal","Europe/Lisbon",-1);
        add("amsterdam","阿姆斯特丹","Amsterdam","荷兰","Netherlands","Europe/Amsterdam",-1);
        add("zurich","苏黎世","Zurich","瑞士","Switzerland","Europe/Zurich",-1);
        add("vienna","维也纳","Vienna","奥地利","Austria","Europe/Vienna",-1);
        add("stockholm","斯德哥尔摩","Stockholm","瑞典","Sweden","Europe/Stockholm",-1);
        add("oslo","奥斯陆","Oslo","挪威","Norway","Europe/Oslo",-1);
        add("helsinki","赫尔辛基","Helsinki","芬兰","Finland","Europe/Helsinki",-1);
        add("athens","雅典","Athens","希腊","Greece","Europe/Athens",-1);
        add("istanbul","伊斯坦布尔","Istanbul","土耳其","Türkiye","Europe/Istanbul",-1);
        add("moscow","莫斯科","Moscow","俄罗斯","Russia","Europe/Moscow",-1);
        add("reykjavik","雷克雅未克","Reykjavík","冰岛","Iceland","Atlantic/Reykjavik",-1);
        add("cairo","开罗","Cairo","埃及","Egypt","Africa/Cairo",-1);
        add("johannesburg","约翰内斯堡","Johannesburg","南非","South Africa","Africa/Johannesburg",-1);
        add("nairobi","内罗毕","Nairobi","肯尼亚","Kenya","Africa/Nairobi",-1);
        add("lagos","拉各斯","Lagos","尼日利亚","Nigeria","Africa/Lagos",-1);
        add("casablanca","卡萨布兰卡","Casablanca","摩洛哥","Morocco","Africa/Casablanca",-1);
        add("accra","阿克拉","Accra","加纳","Ghana","Africa/Accra",-1);
        group("中国","China","Asia/Shanghai","tianjin|天津|Tianjin","wuhan|武汉|Wuhan","nanjing|南京|Nanjing","hangzhou|杭州|Hangzhou","xian|西安|Xi'an","suzhou|苏州|Suzhou","qingdao|青岛|Qingdao","xiamen|厦门|Xiamen","fuzhou|福州|Fuzhou","harbin|哈尔滨|Harbin","shenyang|沈阳|Shenyang","dalian|大连|Dalian","changsha|长沙|Changsha","zhengzhou|郑州|Zhengzhou","hefei|合肥|Hefei","kunming|昆明|Kunming","ningbo|宁波|Ningbo","jinan|济南|Jinan","nanning|南宁|Nanning","haikou|海口|Haikou","sanya|三亚|Sanya","dongguan|东莞|Dongguan","foshan|佛山|Foshan","wuxi|无锡|Wuxi","guiyang|贵阳|Guiyang","nanchang|南昌|Nanchang","shijiazhuang|石家庄|Shijiazhuang","taiyuan|太原|Taiyuan","lanzhou|兰州|Lanzhou","yinchuan|银川|Yinchuan","hohhot|呼和浩特|Hohhot","changchun|长春|Changchun","wenzhou|温州|Wenzhou","zhuhai|珠海|Zhuhai","quanzhou|泉州|Quanzhou","shaoxing|绍兴|Shaoxing");
        group("中国","China","Asia/Shanghai","urumqi|乌鲁木齐|Ürümqi","lhasa|拉萨|Lhasa");
        group("日本","Japan","Asia/Tokyo","kyoto|京都|Kyoto","nagoya|名古屋|Nagoya","yokohama|横滨|Yokohama","sapporo|札幌|Sapporo","fukuoka|福冈|Fukuoka","hiroshima|广岛|Hiroshima","kobe|神户|Kobe","sendai|仙台|Sendai","naha|那霸|Naha","kawasaki|川崎|Kawasaki");
        group("韩国","South Korea","Asia/Seoul","busan|釜山|Busan","incheon|仁川|Incheon","daegu|大邱|Daegu","daejeon|大田|Daejeon","gwangju|光州|Gwangju","jeju|济州|Jeju City");
        group("越南","Vietnam","Asia/Ho_Chi_Minh","danang|岘港|Da Nang","haiphong|海防|Haiphong","cantho|芹苴|Cần Thơ");
        group("泰国","Thailand","Asia/Bangkok","chiangmai|清迈|Chiang Mai","pattaya|芭堤雅|Pattaya");
        group("柬埔寨","Cambodia","Asia/Phnom_Penh","phnompenh|金边|Phnom Penh","siemreap|暹粒|Siem Reap");
        group("老挝","Laos","Asia/Vientiane","vientiane|万象|Vientiane");
        group("缅甸","Myanmar","Asia/Yangon","yangon|仰光|Yangon","mandalay|曼德勒|Mandalay");
        group("菲律宾","Philippines","Asia/Manila","cebu|宿务|Cebu City","davao|达沃|Davao City","quezoncity|奎松市|Quezon City");
        group("马来西亚","Malaysia","Asia/Kuala_Lumpur","penang|槟城|George Town, Penang","johorbahru|新山|Johor Bahru","kotakinabalu|亚庇|Kota Kinabalu");
        group("印度尼西亚","Indonesia","Asia/Jakarta","bandung|万隆|Bandung","semarang|三宝垄|Semarang","yogyakarta|日惹|Yogyakarta","medan|棉兰|Medan","palembang|巨港|Palembang","surabaya|泗水|Surabaya");
        group("印度尼西亚","Indonesia","Asia/Makassar","makassar|望加锡|Makassar");
        group("文莱","Brunei","Asia/Brunei","bandarseribegawan|斯里巴加湾市|Bandar Seri Begawan");
        group("印度","India","Asia/Kolkata","kolkata|加尔各答|Kolkata","chennai|金奈|Chennai","bengaluru|班加罗尔|Bengaluru","hyderabad|海得拉巴|Hyderabad","pune|浦那|Pune","ahmedabad|艾哈迈达巴德|Ahmedabad","jaipur|斋浦尔|Jaipur","surat|苏拉特|Surat","lucknow|勒克瑙|Lucknow","kanpur|坎普尔|Kanpur","nagpur|那格浦尔|Nagpur","indore|印多尔|Indore","patna|巴特那|Patna","bhopal|博帕尔|Bhopal","visakhapatnam|维沙卡帕特南|Visakhapatnam","kochi|科钦|Kochi, India");
        group("巴基斯坦","Pakistan","Asia/Karachi","islamabad|伊斯兰堡|Islamabad","lahore|拉合尔|Lahore","faisalabad|费萨拉巴德|Faisalabad","rawalpindi|拉瓦尔品第|Rawalpindi");
        group("孟加拉国","Bangladesh","Asia/Dhaka","chittagong|吉大港|Chattogram","khulna|库尔纳|Khulna");
        group("阿联酋","United Arab Emirates","Asia/Dubai","abudhabi|阿布扎比|Abu Dhabi","sharjah|沙迦|Sharjah");
        group("沙特阿拉伯","Saudi Arabia","Asia/Riyadh","jeddah|吉达|Jeddah","mecca|麦加|Mecca","medina|麦地那|Medina");
        group("阿曼","Oman","Asia/Muscat","muscat|马斯喀特|Muscat");
        group("科威特","Kuwait","Asia/Kuwait","kuwaitcity|科威特城|Kuwait City");
        group("巴林","Bahrain","Asia/Bahrain","manama|麦纳麦|Manama");
        group("伊拉克","Iraq","Asia/Baghdad","baghdad|巴格达|Baghdad","basra|巴士拉|Basra");
        group("黎巴嫩","Lebanon","Asia/Beirut","beirut|贝鲁特|Beirut");
        group("约旦","Jordan","Asia/Amman","amman|安曼|Amman");
        group("阿塞拜疆","Azerbaijan","Asia/Baku","baku|巴库|Baku");
        group("格鲁吉亚","Georgia","Asia/Tbilisi","tbilisi|第比利斯|Tbilisi");
        group("亚美尼亚","Armenia","Asia/Yerevan","yerevan|埃里温|Yerevan");
        group("哈萨克斯坦","Kazakhstan","Asia/Almaty","almaty|阿拉木图|Almaty","astana|阿斯塔纳|Astana");
        group("乌兹别克斯坦","Uzbekistan","Asia/Tashkent","tashkent|塔什干|Tashkent");
        group("澳大利亚","Australia","Australia/Sydney","canberra|堪培拉|Canberra","newcastleaustralia|纽卡斯尔|Newcastle, New South Wales");
        group("澳大利亚","Australia","Australia/Brisbane","goldcoast|黄金海岸|Gold Coast, Queensland");
        group("澳大利亚","Australia","Australia/Hobart","hobart|霍巴特|Hobart");
        group("新西兰","New Zealand","Pacific/Auckland","queenstown|皇后镇|Queenstown, New Zealand","hamiltonnz|汉密尔顿|Hamilton, New Zealand","dunedin|但尼丁|Dunedin");
        group("巴布亚新几内亚","Papua New Guinea","Pacific/Port_Moresby","portmoresby|莫尔兹比港|Port Moresby");
        group("美国","United States","America/New_York","miami|迈阿密|Miami","atlanta|亚特兰大|Atlanta","philadelphia|费城|Philadelphia","detroit|底特律|Detroit","orlando|奥兰多|Orlando","tampa|坦帕|Tampa","charlotte|夏洛特|Charlotte, North Carolina","pittsburgh|匹兹堡|Pittsburgh","baltimore|巴尔的摩|Baltimore");
        group("美国","United States","America/Chicago","houston|休斯敦|Houston","dallas|达拉斯|Dallas","austin|奥斯汀|Austin","minneapolis|明尼阿波利斯|Minneapolis","nashville|纳什维尔|Nashville","neworleans|新奥尔良|New Orleans","sanantonio|圣安东尼奥|San Antonio","kansascity|堪萨斯城|Kansas City, Missouri","stlouis|圣路易斯|St. Louis");
        group("美国","United States","America/Denver","saltlakecity|盐湖城|Salt Lake City","albuquerque|阿尔伯克基|Albuquerque");
        group("美国","United States","America/Los_Angeles","sandiego|圣迭戈|San Diego","lasvegas|拉斯维加斯|Las Vegas","portland|波特兰|Portland, Oregon","sanjose|圣何塞|San Jose, California","sacramento|萨克拉门托|Sacramento","fresno|弗雷斯诺|Fresno");
        group("加拿大","Canada","America/Toronto","montreal|蒙特利尔|Montreal","ottawa|渥太华|Ottawa","quebeccity|魁北克城|Quebec City","hamiltonca|汉密尔顿|Hamilton, Ontario");
        group("加拿大","Canada","America/Edmonton","calgary|卡尔加里|Calgary","edmonton|埃德蒙顿|Edmonton");
        group("加拿大","Canada","America/Winnipeg","winnipeg|温尼伯|Winnipeg");
        group("墨西哥","Mexico","America/Mexico_City","guadalajara|瓜达拉哈拉|Guadalajara","puebla|普埃布拉|Puebla","leonmexico|莱昂|León, Guanajuato","queretaro|克雷塔罗|Querétaro City");
        group("墨西哥","Mexico","America/Monterrey","monterrey|蒙特雷|Monterrey");
        group("墨西哥","Mexico","America/Cancun","cancun|坎昆|Cancún");
        group("巴拿马","Panama","America/Panama","panamacity|巴拿马城|Panama City");
        group("古巴","Cuba","America/Havana","havana|哈瓦那|Havana");
        group("巴西","Brazil","America/Sao_Paulo","riodejaneiro|里约热内卢|Rio de Janeiro","belohorizonte|贝洛奥里藏特|Belo Horizonte","portoalegre|阿雷格里港|Porto Alegre","curitiba|库里蒂巴|Curitiba","campinas|坎皮纳斯|Campinas");
        group("巴西","Brazil","America/Bahia","salvador|萨尔瓦多|Salvador, Bahia");
        group("巴西","Brazil","America/Recife","recife|累西腓|Recife","fortaleza|福塔莱萨|Fortaleza");
        group("巴西","Brazil","America/Manaus","manaus|马瑙斯|Manaus");
        group("巴西","Brazil","America/Sao_Paulo","brasilia|巴西利亚|Brasília");
        group("厄瓜多尔","Ecuador","America/Guayaquil","quito|基多|Quito","guayaquil|瓜亚基尔|Guayaquil");
        group("哥伦比亚","Colombia","America/Bogota","medellin|麦德林|Medellín","cali|卡利|Cali","barranquilla|巴兰基亚|Barranquilla");
        group("委内瑞拉","Venezuela","America/Caracas","caracas|加拉加斯|Caracas","maracaibo|马拉开波|Maracaibo");
        group("乌拉圭","Uruguay","America/Montevideo","montevideo|蒙得维的亚|Montevideo");
        group("巴拉圭","Paraguay","America/Asuncion","asuncion|亚松森|Asunción");
        group("玻利维亚","Bolivia","America/La_Paz","lapaz|拉巴斯|La Paz","santacruz|圣克鲁斯|Santa Cruz de la Sierra");
        group("秘鲁","Peru","America/Lima","cusco|库斯科|Cusco","arequipa|阿雷基帕|Arequipa");
        group("智利","Chile","America/Santiago","valparaiso|瓦尔帕莱索|Valparaíso");
        group("阿根廷","Argentina","America/Argentina/Cordoba","cordoba|科尔多瓦|Córdoba, Argentina");
        group("阿根廷","Argentina","America/Argentina/Mendoza","mendoza|门多萨|Mendoza, Argentina");
        group("英国","United Kingdom","Europe/London","edinburgh|爱丁堡|Edinburgh","glasgow|格拉斯哥|Glasgow","manchester|曼彻斯特|Manchester","birminghamuk|伯明翰|Birmingham","liverpool|利物浦|Liverpool","bristol|布里斯托尔|Bristol","leeds|利兹|Leeds","belfast|贝尔法斯特|Belfast");
        group("法国","France","Europe/Paris","lyon|里昂|Lyon","marseille|马赛|Marseille","toulouse|图卢兹|Toulouse","nice|尼斯|Nice","bordeaux|波尔多|Bordeaux","lille|里尔|Lille");
        group("德国","Germany","Europe/Berlin","munich|慕尼黑|Munich","frankfurt|法兰克福|Frankfurt","hamburg|汉堡|Hamburg","cologne|科隆|Cologne","dusseldorf|杜塞尔多夫|Düsseldorf","stuttgart|斯图加特|Stuttgart");
        group("西班牙","Spain","Europe/Madrid","barcelona|巴塞罗那|Barcelona","valencia|瓦伦西亚|Valencia","seville|塞维利亚|Seville","malaga|马拉加|Málaga","bilbao|毕尔巴鄂|Bilbao");
        group("意大利","Italy","Europe/Rome","milan|米兰|Milan","naples|那不勒斯|Naples","turin|都灵|Turin","venice|威尼斯|Venice","florence|佛罗伦萨|Florence","bologna|博洛尼亚|Bologna");
        group("葡萄牙","Portugal","Europe/Lisbon","porto|波尔图|Porto");
        group("瑞士","Switzerland","Europe/Zurich","geneva|日内瓦|Geneva","basel|巴塞尔|Basel","bern|伯尔尼|Bern");
        group("比利时","Belgium","Europe/Brussels","brussels|布鲁塞尔|Brussels","antwerp|安特卫普|Antwerp");
        group("丹麦","Denmark","Europe/Copenhagen","copenhagen|哥本哈根|Copenhagen");
        group("爱尔兰","Ireland","Europe/Dublin","dublin|都柏林|Dublin");
        group("捷克","Czechia","Europe/Prague","prague|布拉格|Prague");
        group("匈牙利","Hungary","Europe/Budapest","budapest|布达佩斯|Budapest");
        group("波兰","Poland","Europe/Warsaw","warsaw|华沙|Warsaw","krakow|克拉科夫|Kraków","wroclaw|弗罗茨瓦夫|Wrocław");
        group("罗马尼亚","Romania","Europe/Bucharest","bucharest|布加勒斯特|Bucharest");
        group("保加利亚","Bulgaria","Europe/Sofia","sofia|索非亚|Sofia");
        group("塞尔维亚","Serbia","Europe/Belgrade","belgrade|贝尔格莱德|Belgrade");
        group("克罗地亚","Croatia","Europe/Zagreb","zagreb|萨格勒布|Zagreb");
        group("斯洛文尼亚","Slovenia","Europe/Ljubljana","ljubljana|卢布尔雅那|Ljubljana");
        group("斯洛伐克","Slovakia","Europe/Bratislava","bratislava|布拉迪斯拉发|Bratislava");
        group("乌克兰","Ukraine","Europe/Kyiv","kyiv|基辅|Kyiv","odesa|敖德萨|Odesa","kharkiv|哈尔科夫|Kharkiv");
        group("俄罗斯","Russia","Europe/Moscow","saintpetersburg|圣彼得堡|Saint Petersburg","nizhnynovgorod|下诺夫哥罗德|Nizhny Novgorod","kazan|喀山|Kazan");
        group("俄罗斯","Russia","Asia/Yekaterinburg","yekaterinburg|叶卡捷琳堡|Yekaterinburg");
        group("俄罗斯","Russia","Asia/Novosibirsk","novosibirsk|新西伯利亚|Novosibirsk");
        group("俄罗斯","Russia","Asia/Vladivostok","vladivostok|符拉迪沃斯托克|Vladivostok");
        group("土耳其","Türkiye","Europe/Istanbul","ankara|安卡拉|Ankara","izmir|伊兹密尔|İzmir","antalya|安塔利亚|Antalya","bursa|布尔萨|Bursa");
        group("埃及","Egypt","Africa/Cairo","alexandria|亚历山大|Alexandria","giza|吉萨|Giza");
        group("南非","South Africa","Africa/Johannesburg","capetown|开普敦|Cape Town","durban|德班|Durban","pretoria|比勒陀利亚|Pretoria");
        group("埃塞俄比亚","Ethiopia","Africa/Addis_Ababa","addisababa|亚的斯亚贝巴|Addis Ababa");
        group("坦桑尼亚","Tanzania","Africa/Dar_es_Salaam","daressalaam|达累斯萨拉姆|Dar es Salaam","dodoma|多多马|Dodoma");
        group("乌干达","Uganda","Africa/Kampala","kampala|坎帕拉|Kampala");
        group("卢旺达","Rwanda","Africa/Kigali","kigali|基加利|Kigali");
        group("突尼斯","Tunisia","Africa/Tunis","tunis|突尼斯市|Tunis");
        group("阿尔及利亚","Algeria","Africa/Algiers","algiers|阿尔及尔|Algiers","oran|奥兰|Oran");
        group("摩洛哥","Morocco","Africa/Casablanca","rabat|拉巴特|Rabat","marrakech|马拉喀什|Marrakesh","fes|非斯|Fes");
        group("塞内加尔","Senegal","Africa/Dakar","dakar|达喀尔|Dakar");
        group("科特迪瓦","Ivory Coast","Africa/Abidjan","abidjan|阿比让|Abidjan");
        group("尼日利亚","Nigeria","Africa/Lagos","abuja|阿布贾|Abuja","kano|卡诺|Kano","ibadan|伊巴丹|Ibadan","portharcourt|哈科特港|Port Harcourt");
        group("刚果（金）","DR Congo","Africa/Kinshasa","kinshasa|金沙萨|Kinshasa");
        group("安哥拉","Angola","Africa/Luanda","luanda|罗安达|Luanda");
        group("马达加斯加","Madagascar","Indian/Antananarivo","antananarivo|塔那那利佛|Antananarivo");

        // Keep one representative city for each country's distinct time-zone rules.
        // Beijing and Hong Kong are the deliberate China exceptions requested by product design.
        java.util.Set<String> kept=new java.util.HashSet<>(java.util.Arrays.asList(
            "beijing","hongkong","tokyo","seoul","singapore","bangkok","hanoi","kualalumpur",
            "jakarta","bali","manila","delhi","kathmandu","dhaka","colombo","karachi","dubai",
            "riyadh","doha","tehran","jerusalem","sydney","brisbane","perth","adelaide","darwin",
            "auckland","chatham","suva","honolulu","newyork","losangeles","chicago","denver",
            "phoenix","anchorage","toronto","vancouver","stjohns","calgary","winnipeg","mexicocity",
            "cancun","saopaulo","manaus","buenosaires","santiago","lima","bogota","london","paris",
            "berlin","rome","madrid","lisbon","amsterdam","zurich","vienna","stockholm","oslo",
            "helsinki","athens","istanbul","moscow","yekaterinburg","novosibirsk","vladivostok",
            "reykjavik","cairo","johannesburg","nairobi","lagos","casablanca","accra","phnompenh",
            "vientiane","yangon","bandarseribegawan","muscat","kuwaitcity","manama","baghdad",
            "beirut","amman","baku","tbilisi","yerevan","almaty","tashkent","portmoresby",
            "panamacity","havana","quito","caracas","montevideo","asuncion","lapaz","brussels",
            "copenhagen","dublin","prague","budapest","warsaw","bucharest","sofia","belgrade",
            "zagreb","ljubljana","bratislava","kyiv","addisababa","daressalaam","kampala","kigali",
            "tunis","algiers","dakar","abidjan","kinshasa","luanda","antananarivo"
        ));
        ALL.removeIf(city->!kept.contains(city.id));
    }
    public static List<City> all() { return Collections.unmodifiableList(ALL); }
    public static City find(String id) { for(City c:ALL) if(c.id.equals(id))return c; return null; }
    public String name(boolean chinese) { return chinese?zh:en; }
    public String country(boolean chinese) { return chinese?countryZh:countryEn; }
    public boolean matches(String query) {
        String value=(zh+en+countryZh+countryEn+zone).toLowerCase(Locale.ROOT);
        return value.contains(query.trim().toLowerCase(Locale.ROOT));
    }
    public ZonedDateTime now() { return ZonedDateTime.now(ZoneId.of(zone)); }
    public String time(ZonedDateTime now) { return now.format(DateTimeFormatter.ofPattern("HH:mm",Locale.ROOT)); }
    public String date(ZonedDateTime now, boolean chinese) {
        return now.format(DateTimeFormatter.ofPattern(chinese?"M月d日 EEE":"EEE, MMM d",chinese?Locale.SIMPLIFIED_CHINESE:Locale.ENGLISH));
    }
    public String offset(ZonedDateTime now) {
        int mins=now.getOffset().getTotalSeconds()/60,abs=Math.abs(mins);
        return "UTC "+(mins<0?"−":"+")+(abs/60)+(abs%60==0?"":String.format(Locale.ROOT,":%02d",abs%60));
    }
    public String zoneName(ZonedDateTime now, boolean chinese) {
        TimeZone tz=TimeZone.getTimeZone(zone);
        boolean dst=now.getZone().getRules().isDaylightSavings(now.toInstant());
        String full=tz.getDisplayName(dst,TimeZone.LONG,chinese?Locale.SIMPLIFIED_CHINESE:Locale.ENGLISH);
        return full+" ("+tz.getDisplayName(dst,TimeZone.SHORT,Locale.ENGLISH)+")";
    }
}
