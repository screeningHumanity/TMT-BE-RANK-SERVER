package TMT.Ranking.assetranking.infrastructure;

import static TMT.Ranking.assetranking.domain.QAssetRanking.assetRanking;

import TMT.Ranking.assetranking.domain.AssetRanking;
import TMT.Ranking.assetranking.dto.AssetRankingDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AssetRankingQueryDslImp implements AssetRankingQueryDsl{

    private final JPAQueryFactory jpaQueryFactory;
    @Override //집계된 지갑 정보 update
    @Transactional
    public void updateAssetRanking(AssetRankingDto assetRankingDto){

        jpaQueryFactory
                .update(assetRanking)
                .set(assetRanking.nickname,assetRankingDto.getNickname())
                .set(assetRanking.won,assetRankingDto.getWon())
                .where(assetRanking.uuid.eq(assetRankingDto.getUuid()))
                .execute();

    }

    @Override //자산랭킹 순위부여
    @Transactional
    public void updateRanking(){

        List<AssetRanking> rankings = jpaQueryFactory
                .selectFrom(assetRanking)
                .orderBy(assetRanking.won.desc())
                .fetch();

        long rank = 1;
        long previousProfit = 0;
        long sameProfitCount = 0;

        for (int i = 0; i < rankings.size(); i++) {
            AssetRanking ranking = rankings.get(i);

            if (ranking.getWon() == previousProfit) {
                sameProfitCount++;
            } else {
                rank += sameProfitCount;
                sameProfitCount = 1;
                previousProfit = ranking.getWon();
            }

            jpaQueryFactory.update(assetRanking)
                    .set(assetRanking.ranking, rank)
                    .where(assetRanking.uuid.eq(ranking.getUuid()))
                    .execute();
        }

    }

    @Override
    @Transactional //자산 랭킹 변동 순위 정산
    public void updateRankingChange(){

        NumberExpression<Long> changeRankingExpression = Expressions.cases()//조건부설정
                .when(assetRanking.yesterdayRanking.eq(0L)) //yesterday 가 0일 경우
                .then(0L) //todayranking 반환
                .otherwise(assetRanking.yesterdayRanking.subtract(assetRanking.ranking));

        jpaQueryFactory
                .update(assetRanking)
                .set(assetRanking.changeRanking, changeRankingExpression)
                .execute();
    }

    @Override
    @Transactional //어제 자산랭킹 순위 정산
    public void updateYesterdayRanking(){

        jpaQueryFactory
                .update(assetRanking)
                .set(assetRanking.yesterdayRanking, assetRanking.ranking)
                .execute();
    }

    @Override
    public List<Tuple> getAssetRanking(Pageable pageable) {
        return jpaQueryFactory
                .select(assetRanking.nickname, assetRanking.ranking,
                        assetRanking.won, assetRanking.changeRanking)
                .from(assetRanking)
                .orderBy(assetRanking.won.desc())
                .offset(pageable.getOffset())  // 페이징 처리: 시작 지점
                .limit(pageable.getPageSize()) // 페이징 처리: 페이지 크기
                .fetch();
    }

    @Override
    public long getAssetRankingCount() {
        return jpaQueryFactory
                .selectFrom(assetRanking)
                .fetchCount();  // 전체 레코드 수를 계산
    }

}
