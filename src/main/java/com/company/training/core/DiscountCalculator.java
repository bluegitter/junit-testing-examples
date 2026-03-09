package com.company.training.core;

public class DiscountCalculator {

    /**
     * 计算最终价格
     *
     * @param amount
     * @param customerLevel
     * @return
     */
    public double calculateFinalPrice(double amount, String customerLevel) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be >= 0");
        }
        if (customerLevel == null || customerLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer level must not be blank");
        }

        String normalizedLevel = customerLevel.trim().toUpperCase();
        switch (normalizedLevel) {
            case "VIP":
                return amount * 0.8;
            case "INTERN":
                return amount * 0.9;
            case "NORMAL":
                return amount;
            default:
                throw new IllegalArgumentException("Unsupported customer level: " + customerLevel);
        }
    }

    /**
     * 判断是否包邮
     *
     * @param amount
     * @return
     */
    public boolean isFreeShipping(double amount) {
        return amount >= 99;
    }
}
