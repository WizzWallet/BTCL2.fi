package com.wizz.fi.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wizz.fi.dao.pojo.LoginUser;
import com.wizz.fi.dto.StakeOrdinalDTO;
import com.wizz.fi.dto.StakeTokenDTO;
import com.wizz.fi.service.StakeService;
import com.wizz.fi.sso.CurrentUser;
import com.wizz.fi.sso.UnAuthorization;
import com.wizz.fi.util.CommonResult;
import com.wizz.fi.vo.OrderVO;
import com.wizz.fi.vo.OrdinalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/")
public class StakeAPI {
    @Autowired
    private StakeService stakeService;

    @RequestMapping(value = "/stake/ordinal", method = RequestMethod.POST)
    public CommonResult<OrderVO> stakeOrdinal(@RequestBody StakeOrdinalDTO stakeOrdinalDTO, @CurrentUser LoginUser loginUser) throws Exception {
        return CommonResult.success(stakeService.stakeOrdinal(loginUser.getUserAddress(), stakeOrdinalDTO.getInputAddress(), stakeOrdinalDTO.getTxid(), stakeOrdinalDTO.getOutputAddress()));
    }

    @RequestMapping(value = "/stake/token", method = RequestMethod.POST)
    public CommonResult<OrderVO> stakeToken(@RequestBody StakeTokenDTO stakeTokenDTO, @CurrentUser LoginUser loginUser) throws Exception {
        return CommonResult.success(stakeService.stakeToken(loginUser.getUserAddress(), stakeTokenDTO.getInputAddress(), stakeTokenDTO.getTxid(), stakeTokenDTO.getOutputAddress()));
    }

    @RequestMapping(value = "/ordinals", method = RequestMethod.GET)
    @UnAuthorization
    public CommonResult<Page<OrdinalVO>> ordinals(@RequestParam(value = "inscriptionNumber", required = false) String inscriptionNumber,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return CommonResult.success(stakeService.listOrdinals(inscriptionNumber, pageNum, pageSize));
    }
}
