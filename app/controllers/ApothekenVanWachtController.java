package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.ApotheekVanWacht;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import play.libs.WS;
import play.mvc.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Double.parseDouble;

public class ApothekenVanWachtController extends Controller {

    private static void customRenderJSON(Object o) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
        renderJSON(gson.toJson(o).toString());
    }

    public static void zoekApothekenVanWacht(float latitude, float longitude) throws DocumentException, ParseException {
        List<ApotheekVanWacht> apothekenVanWacht = zoekApotheekVanWacht(new Coordinaten(latitude, longitude));
        customRenderJSON(apothekenVanWacht);
    }

    private static List<ApotheekVanWacht> zoekApotheekVanWacht(Coordinaten coordinaten) throws DocumentException, ParseException {
        List<ApotheekVanWacht> apothekenVanWacht = newArrayList();
        Date now = new Date();
        WS.HttpResponse response = WS.url("http://admin.ringring.be/apb/public/duty_xml.asp?lang=1&lat=" + coordinaten.latitude + "&lng=" + coordinaten.longitude + "&date=" + dag(now) + "&hour=" + uur(now)).get();
        Document document = new SAXReader().read(response.getStream());
        for (Element pharmacyEl : (List<Element>) document.selectNodes("//pharmacy")) {
            apothekenVanWacht.add(alsApotheekVanWacht(pharmacyEl));
        }
        return apothekenVanWacht;
    }

    private static ApotheekVanWacht alsApotheekVanWacht(Element pharmacyEl) throws ParseException {
        String naam = pharmacyEl.selectSingleNode("name").getText();
        String straatEnNummer = pharmacyEl.selectSingleNode("address").getText();
        String stad = pharmacyEl.selectSingleNode("city").getText();
        String tel = pharmacyEl.selectSingleNode("phone").getText();
        Node urlNode = pharmacyEl.selectSingleNode("url");
        String url = null;
        if(urlNode != null) {
            url = urlNode.getText();
        }
        Date eindDatum = eindDatum(pharmacyEl.selectSingleNode("hours").getText());
        Double afstand = parseDouble(pharmacyEl.attributeValue("distance"));
        return new ApotheekVanWacht(naam, straatEnNummer, stad, tel, url, afstand, eindDatum);
    }

    private static Date eindDatum(String hours) throws ParseException {
        String[] hoursSplitted = hours.split("<br/>");
        String latestHours = hoursSplitted[hoursSplitted.length - 1];
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(StringUtils.left(latestHours, 10) + " " + StringUtils.right(latestHours, 5));
    }

    private static String uur(Date date) {
        return new SimpleDateFormat("HHmm").format(date);
    }

    private static String dag(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }
}
