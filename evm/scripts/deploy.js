// 1. 首先，确保你有一个 Hardhat 项目设置好了。如果没有，可以这样初始化：
// npx hardhat init

// 2. 在你的项目中创建一个部署脚本，例如 scripts/deploy.js：

const hre = require("hardhat");

async function main() {
  const [deployer] = await hre.ethers.getSigners();

  // 获取合约工厂
  const wToothyContract = await hre.ethers.getContractFactory("wToothy");
  
  // 部署合约
  const wToothy = await wToothyContract.deploy();
  
  // 等待部署完成
  await wToothy.waitForDeployment();

  console.log("wToothy deployed to:", await wToothy.getAddress());
  console.log("Owner set to:", await wToothy.owner());
}

// 运行部署函数
main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });

// 3. 在终端中运行部署脚本：
// npx hardhat run scripts/deploy.js --network <network-name>