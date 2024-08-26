package com.wizz.fi.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wizz.fi.dto.StakeOrdinalDTO;
import com.wizz.fi.dto.StakeTokenDTO;
import com.wizz.fi.service.OrderService;
import com.wizz.fi.service.StakeService;
import com.wizz.fi.util.CommonResult;
import com.wizz.fi.vo.OrderVO;
import com.wizz.fi.vo.OrdinalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/")
public class StakeAPI {
    @Autowired
    private StakeService stakeService;

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/stake/ordinal", method = RequestMethod.POST)
    public CommonResult<OrderVO> stakeOrdinal(@RequestBody StakeOrdinalDTO stakeOrdinalDTO) throws Exception {
        return CommonResult.success(stakeService.stakeOrdinal(stakeOrdinalDTO.getInputAddress(), stakeOrdinalDTO.getTxid(), stakeOrdinalDTO.getOutputAddress(), stakeOrdinalDTO.getOrdinals()));
    }

    @RequestMapping(value = "/stake/token", method = RequestMethod.POST)
    public CommonResult<OrderVO> stakeToken(@RequestBody StakeTokenDTO stakeTokenDTO) throws Exception {
        return CommonResult.success(stakeService.stakeToken(stakeTokenDTO.getInputAddress(), stakeTokenDTO.getTxid(), stakeTokenDTO.getOutputAddress(), stakeTokenDTO.getOrdinals()));
    }

    @RequestMapping(value = "/ordinals", method = RequestMethod.GET)
    public CommonResult<Page<OrdinalVO>> ordinals(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return CommonResult.success(stakeService.listOrdinals(pageNum, pageSize));
    }

    @RequestMapping(value = "/user_ordinals", method = RequestMethod.GET)
    public CommonResult<List<OrdinalVO>> user_ordinals(@RequestParam(value = "address", required = false) String address) {
        return CommonResult.success(stakeService.listUserOrdinals(address));
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public CommonResult<List<OrderVO>> orders(@RequestParam(value = "address", required = false) String address) {
        return CommonResult.success(orderService.list(address));
    }
}
