package TMT.Ranking.assetranking.presentation;


import TMT.Ranking.assetranking.application.AssetRankingServiceImp;
import TMT.Ranking.assetranking.vo.AssetRankingResponseVo;
import TMT.Ranking.assetranking.vo.MyAssetRankingResponseVo;
import TMT.Ranking.global.common.response.BaseResponse;
import TMT.Ranking.global.common.token.DecodingToken;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AssetRankingController {

    private final AssetRankingServiceImp assetRankingServiceImp;
    private final DecodingToken decodingToken;


    @GetMapping("/asset") //랭킹 정보 return
    public BaseResponse<List<AssetRankingResponseVo>> getProfit(){

        List<AssetRankingResponseVo> assetRankingResponseVo =
                assetRankingServiceImp.getAssetRanking();
        return new BaseResponse<>(assetRankingResponseVo);

    }

    @GetMapping("/my-asset") //내 랭킹 등수 조회
    public BaseResponse<MyAssetRankingResponseVo> getMYProfit(@RequestHeader("Authorization")String jwt){

        String uuid = decodingToken.getUuid(jwt);
        MyAssetRankingResponseVo myAssetrankingResponseVo =
                assetRankingServiceImp.getMyAssetRanking(uuid);
        return new BaseResponse<>(myAssetrankingResponseVo);

    }


}