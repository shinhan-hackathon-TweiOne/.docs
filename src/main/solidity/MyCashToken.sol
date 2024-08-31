// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

import "https://github.com/OpenZeppelin/openzeppelin-contracts/blob/v4.8.0/contracts/token/ERC20/ERC20.sol";
import "https://github.com/OpenZeppelin/openzeppelin-contracts/blob/v4.8.0/contracts/access/Ownable.sol";

contract MyCashToken is ERC20, Ownable {

    constructor(uint256 initialSupply) ERC20("MyCash", "MCT") {
        _mint(msg.sender, initialSupply);
    }

    // Only the owner can mint new tokens
    function charge(address to, uint256 amount) external onlyOwner {
        _mint(to, amount);
    }

    // Withdraw tokens by burning from the sender's balance
    function withdraw(uint256 amount) external {
        require(balanceOf(msg.sender) >= amount, "Insufficient balance");
        _burn(msg.sender, amount);
    }

    // The `transfer` function is a basic feature provided by the ERC20 standard.
}
