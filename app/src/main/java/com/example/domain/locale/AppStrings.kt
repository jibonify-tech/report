package com.example.domain.locale

import java.text.NumberFormat
import java.util.Locale

object AppStrings {

    fun formatTaka(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 0
        return "৳" + formatter.format(amount)
    }

    // App Branding & Tagline
    fun appName(lang: AppLanguage) = "Jibonify Work"
    fun appTagline(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "কাজ করুন, আয় করুন, এগিয়ে যান — সব এক প্ল্যাটফর্মে"
        AppLanguage.ENGLISH -> "Work, Earn & Grow — All in One Platform"
    }

    // Navigation Tabs
    fun tabHome(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হোম"
        AppLanguage.ENGLISH -> "Home"
    }
    fun tabMyWork(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আমার কাজ"
        AppLanguage.ENGLISH -> "My Work"
    }
    fun tabWallet(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ওয়ালেট"
        AppLanguage.ENGLISH -> "Wallet"
    }
    fun tabProfile(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "প্রোফাইল"
        AppLanguage.ENGLISH -> "Profile"
    }
    fun tabReferral(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আমার নেটওয়ার্ক"
        AppLanguage.ENGLISH -> "My Network"
    }
    fun tabNotifications(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "নোটিফিকেশন"
        AppLanguage.ENGLISH -> "Notifications"
    }
    fun tabSettings(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সেটিংস"
        AppLanguage.ENGLISH -> "Settings"
    }
    fun tabHelpCenter(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হেল্প সেন্টার"
        AppLanguage.ENGLISH -> "Help Center"
    }

    // Drawer Menu
    fun drawerTelegram(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "টেলিগ্রাম গ্রুপ"
        AppLanguage.ENGLISH -> "Telegram Community"
    }
    fun drawerFacebook(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ফেসবুক পেজ"
        AppLanguage.ENGLISH -> "Facebook Page"
    }
    fun drawerYoutube(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ইউটিউব চ্যানেল"
        AppLanguage.ENGLISH -> "YouTube Channel"
    }
    fun drawerWebsite(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "অফিসিয়াল ওয়েবসাইট"
        AppLanguage.ENGLISH -> "Official Website"
    }
    fun drawerLogout(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "লগআউট"
        AppLanguage.ENGLISH -> "Logout"
    }

    // Greetings & Balance Summary
    fun greetingSalam(lang: AppLanguage, name: String) = when (lang) {
        AppLanguage.BANGLA -> "আসসালামু আলাইকুম, $name 👋"
        AppLanguage.ENGLISH -> "Welcome back, $name 👋"
    }
    fun mainBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মূল ব্যালেন্স"
        AppLanguage.ENGLISH -> "Main Balance"
    }
    fun bonusBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বোনাস ব্যালেন্স"
        AppLanguage.ENGLISH -> "Bonus Balance"
    }
    fun totalEarnings(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মোট আয়"
        AppLanguage.ENGLISH -> "Total Earnings"
    }
    fun pendingBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পেন্ডিং ব্যালেন্স"
        AppLanguage.ENGLISH -> "Pending Balance"
    }
    fun totalWithdrawn(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মোট উইথড্র"
        AppLanguage.ENGLISH -> "Total Withdrawn"
    }

    // Home Quick Actions
    fun actionAddMoney(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "টাকা যোগ"
        AppLanguage.ENGLISH -> "Add Money"
    }
    fun actionWithdraw(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "উইথড্র"
        AppLanguage.ENGLISH -> "Withdraw"
    }
    fun actionTransfer(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ট্রান্সফার"
        AppLanguage.ENGLISH -> "Transfer"
    }
    fun actionHistory(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "হিস্ট্রি"
        AppLanguage.ENGLISH -> "History"
    }
    fun actionDailySpin(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "অটো স্পিন"
        AppLanguage.ENGLISH -> "Auto Spin"
    }

    // Service Section
    fun ourServices(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আমাদের সার্ভিস"
        AppLanguage.ENGLISH -> "Our Services"
    }
    fun viewAll(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সব দেখুন"
        AppLanguage.ENGLISH -> "View All"
    }
    fun startService(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সার্ভিস শুরু করুন"
        AppLanguage.ENGLISH -> "Start Service"
    }
    fun insufficientBalanceTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আপনার ব্যালেন্স পর্যাপ্ত নয়"
        AppLanguage.ENGLISH -> "Insufficient Balance"
    }
    fun insufficientBalanceDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "এই সার্ভিসটি চালু করতে ওয়ালেটে পর্যাপ্ত টাকা নেই। অনুগ্রহ করে টাকা যোগ করুন।"
        AppLanguage.ENGLISH -> "You do not have enough funds to start this service. Please deposit funds."
    }

    // Onboarding
    fun onb1Title(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "এক প্ল্যাটফর্মে অনেক সুযোগ"
        AppLanguage.ENGLISH -> "Opportunities in One Platform"
    }
    fun onb1Desc(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ডিজিটাল কাজ, সার্ভিস ও আয়ের বিভিন্ন সুযোগ এক জায়গায়।"
        AppLanguage.ENGLISH -> "Digital work, essential services and multiple earning streams in one place."
    }
    fun onb2Title(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সহজে কাজ করুন"
        AppLanguage.ENGLISH -> "Work with Ease"
    }
    fun onb2Desc(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আপনার মোবাইল থেকেই দ্রুত ও সহজভাবে কাজ পরিচালনা করুন।"
        AppLanguage.ENGLISH -> "Manage, execute and submit tasks quickly and effortlessly right from your phone."
    }
    fun onb3Title(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আয় ও ব্যালেন্স ট্র্যাক করুন"
        AppLanguage.ENGLISH -> "Track Earnings & Wallet"
    }
    fun onb3Desc(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আপনার আয়, ওয়ালেট, রেফারেল ও লেনদেন এক জায়গা থেকে দেখুন।"
        AppLanguage.ENGLISH -> "Real-time tracking of your wallet balance, referral commissions, and instant payouts."
    }
    fun btnSkip(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "স্কিপ"
        AppLanguage.ENGLISH -> "Skip"
    }
    fun btnNext(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পরবর্তী"
        AppLanguage.ENGLISH -> "Next"
    }
    fun btnGetStarted(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "শুরু করুন"
        AppLanguage.ENGLISH -> "Get Started"
    }

    // Auth
    fun loginTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "লগইন করুন"
        AppLanguage.ENGLISH -> "Welcome Back"
    }
    fun loginSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আপনার একাউন্টে প্রবেশ করতে তথ্য দিন"
        AppLanguage.ENGLISH -> "Enter your credentials to continue"
    }
    fun registerTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "নতুন একাউন্ট খুলুন"
        AppLanguage.ENGLISH -> "Create Account"
    }
    fun mobileOrEmail(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মোবাইল নম্বর অথবা ইমেইল"
        AppLanguage.ENGLISH -> "Mobile Number or Email"
    }
    fun fullName(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পূর্ণ নাম"
        AppLanguage.ENGLISH -> "Full Name"
    }
    fun password(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পাসওয়ার্ড"
        AppLanguage.ENGLISH -> "Password"
    }
    fun confirmPassword(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পাসওয়ার্ড নিশ্চিত করুন"
        AppLanguage.ENGLISH -> "Confirm Password"
    }
    fun referralCodeOptional(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "রেফারেল কোড (ঐচ্ছিক)"
        AppLanguage.ENGLISH -> "Referral Code (Optional)"
    }
    fun forgotPassword(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পাসওয়ার্ড ভুলে গেছেন?"
        AppLanguage.ENGLISH -> "Forgot Password?"
    }
    fun dontHaveAccount(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "একাউন্ট নেই? রেজিস্ট্রেশন করুন"
        AppLanguage.ENGLISH -> "Don't have an account? Sign Up"
    }
    fun alreadyHaveAccount(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ইতিমধ্যে একাউন্ট আছে? লগইন করুন"
        AppLanguage.ENGLISH -> "Already have an account? Login"
    }
    fun demoLoginBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "⚡ ডেমো মোডে প্রবেশ করুন (রহিম আহমেদ)"
        AppLanguage.ENGLISH -> "⚡ Quick Demo Login (Rahim Ahmed)"
    }

    // Work screen
    fun workSummary(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "কাজের সারসংক্ষেপ"
        AppLanguage.ENGLISH -> "Work Overview"
    }
    fun totalJobs(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মোট কাজ"
        AppLanguage.ENGLISH -> "Total Jobs"
    }
    fun completedJobs(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সম্পন্ন"
        AppLanguage.ENGLISH -> "Completed"
    }
    fun pendingJobs(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পেন্ডিং"
        AppLanguage.ENGLISH -> "Pending"
    }
    fun rejectedJobs(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বাতিল"
        AppLanguage.ENGLISH -> "Rejected"
    }
    fun filterAll(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সব"
        AppLanguage.ENGLISH -> "All"
    }

    // Wallet & Transactions
    fun transactions(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "লেনদেনের বিবরণ"
        AppLanguage.ENGLISH -> "Transaction History"
    }
    fun transactionStatement(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "লেনদেনের বিবরণী (স্টেটমেন্ট)"
        AppLanguage.ENGLISH -> "Transaction Statement"
    }
    fun filterDeposit(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ডিপোজিট"
        AppLanguage.ENGLISH -> "Deposit"
    }
    fun filterWithdraw(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "উইথড্র"
        AppLanguage.ENGLISH -> "Withdraw"
    }
    fun filterCommission(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "কমিশন"
        AppLanguage.ENGLISH -> "Commission"
    }
    fun filterBonus(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বোনাস"
        AppLanguage.ENGLISH -> "Bonus"
    }
    fun depositTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "টাকা ডিপোজিট (Add Money)"
        AppLanguage.ENGLISH -> "Add Money (Deposit)"
    }
    fun withdrawTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "টাকা উত্তোলন (Withdraw)"
        AppLanguage.ENGLISH -> "Withdraw Funds"
    }
    fun selectPaymentMethod(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "পেমেন্ট মাধ্যম বেছে নিন"
        AppLanguage.ENGLISH -> "Select Payment Method"
    }
    fun enterAmount(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "টাকার পরিমাণ"
        AppLanguage.ENGLISH -> "Enter Amount"
    }
    fun senderNumber(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "প্রেরক নম্বর (Sender Number)"
        AppLanguage.ENGLISH -> "Sender Mobile Number"
    }
    fun transactionId(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ট্রানজেকশন আইডি (TrxID)"
        AppLanguage.ENGLISH -> "Transaction ID (TrxID)"
    }
    fun submit(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "জমা দিন"
        AppLanguage.ENGLISH -> "Submit"
    }
    fun cancel(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বাতিল"
        AppLanguage.ENGLISH -> "Cancel"
    }
    fun copy(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "কপি"
        AppLanguage.ENGLISH -> "Copy"
    }
    fun copiedToClipboard(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "কপি করা হয়েছে!"
        AppLanguage.ENGLISH -> "Copied to clipboard!"
    }

    // Referral
    fun referralCode(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "রেফারেল কোড"
        AppLanguage.ENGLISH -> "Referral Code"
    }
    fun shareReferral(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "রেফার লিংক শেয়ার করুন"
        AppLanguage.ENGLISH -> "Share Referral Link"
    }
    fun totalReferralCount(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "মোট রেফারেল"
        AppLanguage.ENGLISH -> "Total Referrals"
    }
    fun activeReferrals(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সক্রিয় রেফারেল"
        AppLanguage.ENGLISH -> "Active Referrals"
    }
    fun referralEarnings(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "রেফার কমিশন"
        AppLanguage.ENGLISH -> "Referral Commission"
    }

    // Support
    fun supportFaq(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "সাধারণ জিজ্ঞাসা (FAQ)"
        AppLanguage.ENGLISH -> "Frequently Asked Questions"
    }
    fun createTicket(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "নতুন টিকিট তৈরি করুন"
        AppLanguage.ENGLISH -> "Create Support Ticket"
    }
    fun ticketSubject(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বিষয় (Subject)"
        AppLanguage.ENGLISH -> "Ticket Subject"
    }
    fun ticketCategory(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ক্যাটাগরি"
        AppLanguage.ENGLISH -> "Category"
    }
    fun ticketDescription(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "বিস্তারিত লিখুন"
        AppLanguage.ENGLISH -> "Detailed Description"
    }

    // Error & Offline State
    fun offlineMessage(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "ইন্টারনেট সংযোগ নেই"
        AppLanguage.ENGLISH -> "No Internet Connection"
    }
    fun serverError(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "দুঃখিত, এই মুহূর্তে সার্ভার থেকে তথ্য পাওয়া যাচ্ছে না। আবার চেষ্টা করুন।"
        AppLanguage.ENGLISH -> "Sorry, unable to fetch data from the server right now. Please try again."
    }
    fun retry(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "আবার চেষ্টা করুন"
        AppLanguage.ENGLISH -> "Try Again"
    }
    fun demoModeNotice(lang: AppLanguage) = when (lang) {
        AppLanguage.BANGLA -> "⚡ ডেমো মোড সক্রিয় — স্থানীয় ডাটাবেসে সমস্ত তথ্য সুরক্ষিত রয়েছে"
        AppLanguage.ENGLISH -> "⚡ Demo Mode Active — Data persisted in local Room Database"
    }
}
