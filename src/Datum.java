import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

@SerializedName("name")
@Expose
private String name;
@SerializedName("tour")
@Expose
private String tour;
@SerializedName("localDate")
@Expose
private String localDate;
@SerializedName("venue")
@Expose
private String venue;
@SerializedName("price")
@Expose
private Integer price;

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getTour() {
return tour;
}

public void setTour(String tour) {
this.tour = tour;
}

public String getLocalDate() {
return localDate;
}

public void setLocalDate(String localDate) {
this.localDate = localDate;
}

public String getVenue() {
return venue;
}

public void setVenue(String venue) {
this.venue = venue;
}

public Integer getPrice() {
return price;
}

public void setPrice(Integer price) {
this.price = price;
}

}
