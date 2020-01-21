package thiovan.submission5.events;

public class SearchEvent {

    public final String keyword;
    public final String type;

    public SearchEvent(String keyword, String type) {
        this.keyword = keyword;
        this.type = type;
    }
}
