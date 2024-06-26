package TMT.Ranking.batch.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DailyRankingDto {

    private String uuid;
    private Long todayWon;
    private double profit;
    private String nickname;

    @Builder
    public DailyRankingDto(String uuid, Long todayWon, double profit,String nickname) {
        this.uuid = uuid;
        this.todayWon = todayWon;
        this.profit = profit;
        this.nickname = nickname;

    }

}
