require("@nomicfoundation/hardhat-toolbox");

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: "0.8.19",
  networks: {
    pwr: {
      url: "https://ethereumplus.pwrlabs.io/",
      chainId: 10023,
      accounts: ["57b6afe20ea8c9b2a3c93c59caa305f57c710f6772d9b7af396949f61511efa4"], // 使用你的账户私钥
    },
  },
};
