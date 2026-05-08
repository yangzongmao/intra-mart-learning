package com.lakala.payment.app.entity.core.validation.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import nablarch.core.util.StringUtil;

/**
 * 拡張バリデーションに関するユーティリティクラス。
 *
 * @since 1.0
 */
public final class VariousValidationUtil {

    /**
     * 隠蔽コンストラクタ
     */
    private VariousValidationUtil() {
    }

    /**
     */
    private static final Set<Character> AVAILABLE_CHARS_FOR_MAIL_ADDRESS;

    static {
        AVAILABLE_CHARS_FOR_MAIL_ADDRESS = new HashSet<Character>(Arrays.asList('$', '%', '&', '\'', '*', '+', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '=',
                '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '^', '_', '`', 'a', 'b',
                'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '!', '#'));
    }

    /**
     * メールアドレスのローカルパートの最大長
     */
    private static final int MAX_LENGTH_OF_LOCAL_PART = 64;
    /**
     * メールアドレスのドメインパートの最大長
     */
    private static final int MAX_LENGTH_OF_DOMAIN_PART = 255;
    /**
     * 郵便番号ハイフンあり
     */
    private static final String POSTALCODE_WITH_HYPHEN = "^[0-9]{3}-[0-9]{4}$";
    /**
     * 郵便番号ハイフンなし
     */
    private static final String POSTALCODE = "^[0-9]{7}$";
    /**
     * インタネットURL
     */
    private static final String INTERNET_URL = "^(http|https)://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
    /**
     * パスワード
     * 【^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!-/:-@[-`{-~])[!-~]{8,48}$】
     */
    private static final String VALID_PASSWORD = "^(?=.*?[a-zA-Z])(?=.*?\\d)[a-zA-Z\\d]{8,16}$";

    /**
     * メールアドレスに関する精査を行う。<br>
     * RFCに則ったローカル部とドメイン部それぞれの桁数精査を行う。</br>
     * アプリケーションで許容するメールアドレス全体の長さはこのメソッドの呼び出し側で精査すること。<br>
     * <br>
     * 精査仕様：<br>
     * ローカル部に関しては、RFC違反のメールアドレスも存在しえる。そのため、ローカル部に対して厳密なチェックを行うことは、
     * ユーザーがメールを登録できない危険性がある。 また、ローカル部に対して厳密なチェックを行わなくても害はないと判断している。
     * よって、ローカル部に対して行う精査は、桁数と文字種に関する精査のみである。<br>
     * また、これらの精査に加えて、JavaMailのアドレス精査を行うAPIを直接呼び出すことで
     * JavaMailでメールを送信する際に形式チェックでエラーとならないことも検証している。
     *
     * <br>
     * メールアドレスに関する精査仕様は下記の通りである。
     * <ul>
     * <li>メールアドレス全体に関する精査仕様</li>
     * <ul>
     * <li>空文字、nullでないこと。</li>
     * <li>メールアドレスとして有効な文字種のみで構成されていること。有効な文字種は、次の通りである。</li>
     * <ul>
     * <li>大文字アルファベット A B C D E F G H I J K L M N O P Q R S T U V W X Y Z</li>
     * <li>小文字アルファベット a b c d e f g h i j k l m n o p q r s t u v w x y z</li>
     * <li>数字 0 1 2 3 4 5 6 7 8 9</li>
     * <li>その他記号 ! # $ % & * + - . / = ? @ ^ _ ` { | } ~</li>
     * </ul>
     * <li>‘@’（アットマーク）が存在し、1つのみであること。</li>
     * </ul>
     * <li>ローカル部に関する精査仕様</li>
     * <ul>
     * <li>メールアドレスの先頭が’@’（アットマーク）ではないこと。（ローカル部が存在すること。）</li>
     * <li>ローカル部が64文字以下であること。</li>
     * </ul>
     * <li>ドメイン部に関する精査仕様</li>
     * <ul>
     * <li>メールアドレスの末尾が’@’（アットマーク）ではないこと。（ドメイン部が存在すること。）</li>
     * <li>ドメイン部が255文字以下であること。</li>
     * <li>ドメイン部の末尾が’.’（ドット）ではないこと。</li>
     * <li>ドメイン部に’.’（ドット）が存在すること。</li>
     * <li>ドメイン部の先頭が’.’（ドット）ではないこと。</li>
     * <li>ドメイン部にて’.’（ドット）が連続していないこと。</li>
     * </ul>
     * </ul>
     *
     * @param value
     *            精査対象文字列
     * @return 上記の精査仕様に則った有効なメールアドレスの場合、{@code true}。
     */
    public static boolean isValidMailAddress(String value) {

        if (StringUtil.isNullOrEmpty(value)) {
            return false;
        }

        // ローカル部の長さチェック
        int indexOfAtMark = value.indexOf('@');
        if (indexOfAtMark > MAX_LENGTH_OF_LOCAL_PART) {
            return false;
        }

        // @に関するチェック
        if (indexOfAtMark <= 0) {
            // @が先頭にある場合
            // @が存在しない場合
            return false;
        }
        if (indexOfAtMark != value.lastIndexOf('@')) {
            // @が二つ以上存在する場合
            return false;
        }
        if (indexOfAtMark == value.length() - 1) {
            // @が末尾に存在する場合
            return false;
        }

        // "."に関するチェック
        String domainPart = value.substring(indexOfAtMark + 1);
        if (domainPart.length() > MAX_LENGTH_OF_DOMAIN_PART || domainPart.endsWith(".") || domainPart.indexOf('.') <= 0 || domainPart.contains("..")) {
            return false;
        }

        // 文字種に関するチェック
        for (int i = 0; i < value.length(); i++) {
            if (!AVAILABLE_CHARS_FOR_MAIL_ADDRESS.contains(value.charAt(i))) {
                return false;
            }
        }

        try {
            new InternetAddress(value, false);
        } catch (AddressException e) {
            return false;
        }

        return true;
    }

    /** 電話番号の桁数パターン */
    private static final int[][] VALID_TEL_LENGTH_PATTERNS = { { 3, 3, 4 }, { 3, 4, 4 }, { 4, 2, 4 }, { 5, 1, 4 }, { 2, 4, 4 }, { 4, 3, 3 } };

    /** ハイフンがある場合の電話番号パターン */
    private static final Pattern TEL_PATTERN_WITH_HYPHEN = Pattern.compile("^(\\d+)-(\\d+)-(\\d+)$");

    /** ハイフンがない場合の電話番号パターン */
    private static final Pattern TEL_PATTERN_WITH_NO_HYPHEN = Pattern.compile("^[\\d]{10,11}$");

    /**
     * 有効な日本の電話番号であるかを精査する。<br>
     * <br>
     * 精査仕様：<br>
     * 電話番号に関する精査仕様は下記の通りである。
     * <ul>
     * <li>null、空白は許容する。</li>
     * <li>数字とハイフン"-"のみで構成されていること。</li>
     * <li>先頭が"0"で始まっていること。</li>
     * <li>桁数のパターンが次のいずれかであること。</li>
     * <table>
     * <tr>
     * <td>桁数パターン</td>
     * <td>例</td>
     * </tr>
     * <tr>
     * <td>3-3-4</td>
     * <td>012-345-6789</td>
     * </tr>
     * <tr>
     * <td>3-4-4</td>
     * <td>012-3456-7890</td>
     * </tr>
     * <tr>
     * <td>4-2-4</td>
     * <td>0123-45-6789</td>
     * </tr>
     * <tr>
     * <td>5-1-4</td>
     * <td>01234-5-6789</td>
     * </tr>
     * <tr>
     * <td>2-4-4</td>
     * <td>01-2345-6789</td>
     * </tr>
     * <td>4-3-3</td>
     * <td>0123-456-789</td>
     * </tr>
     * <tr>
     * <td>10</td>
     * <td>0123456789</td>
     * </tr>
     * <tr>
     * <td>11</td>
     * <td>01234567890</td>
     * </tr>
     * </table>
     * </ul>
     *
     * @param value
     *            精査対象文字列
     * @return 有効な電話番号である場合、{@code true}。
     */
    public static boolean isValidJapaneseTelNum(String value) {

        if (StringUtil.isNullOrEmpty(value)) {
            return true;
        }

        // if (!value.startsWith("0")) {
        // return false;
        // }

        if (value.contains("-")) {
            Matcher matcher = TEL_PATTERN_WITH_HYPHEN.matcher(value);
            if (!matcher.matches() || matcher.groupCount() != 3) {
                return false;
            }
            int area = matcher.group(1).length();
            int city = matcher.group(2).length();
            int subscriber = matcher.group(3).length();
            if (checkLengthPattern(area, city, subscriber)) {
                return true;
            }
        } else {
            if (TEL_PATTERN_WITH_NO_HYPHEN.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 有効な日本の電話番号であるかを精査する。<br>
     * <br>
     * 精査仕様：<br>
     * 電話番号に関する精査仕様は下記の通りである。
     * <ul>
     * <li>null、空白は許容する。</li>
     * <li>数字とハイフン"-"のみで構成されていること。</li>
     * <li>先頭が"0"で始まっていること。</li>
     * <li>桁数のパターンが次のいずれかであること。</li>
     * <table>
     * <tr>
     * <td>桁数パターン</td>
     * <td>例</td>
     * </tr>
     * <tr>
     * <td>3-3-4</td>
     * <td>012-345-6789</td>
     * </tr>
     * <tr>
     * <td>3-4-4</td>
     * <td>012-3456-7890</td>
     * </tr>
     * <tr>
     * <td>4-2-4</td>
     * <td>0123-45-6789</td>
     * </tr>
     * <tr>
     * <td>5-1-4</td>
     * <td>01234-5-6789</td>
     * </tr>
     * <tr>
     * <td>2-4-4</td>
     * <td>01-2345-6789</td>
     * </tr>
     * <td>4-3-3</td>
     * <td>0123-456-789</td>
     * </tr>
     * </table>
     * </ul>
     *
     * @param value
     *            精査対象文字列
     * @return 有効な電話番号である場合、{@code true}。
     */
    public static boolean isValidJapaneseTelNumWithHyphen(String value) {

        if (StringUtil.isNullOrEmpty(value)) {
            return true;
        }

        // if (!value.startsWith("0")) {
        // return false;
        // }

        if (value.contains("-")) {
            Matcher matcher = TEL_PATTERN_WITH_HYPHEN.matcher(value);
            if (!matcher.matches() || matcher.groupCount() != 3) {
                return false;
            }
            int area = matcher.group(1).length();
            int city = matcher.group(2).length();
            int subscriber = matcher.group(3).length();
            if (checkLengthPattern(area, city, subscriber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 日本の電話番号桁数チェック
     *
     * @param area
     *            市外局番桁数
     * @param city
     *            市内局番桁数
     * @param subscriber
     *            加盟者番号桁数
     * @return 市外局番桁数、市内局番桁数、加盟者番号桁数の組が有効な電話番号のパターンであれば、{@code true}。
     */
    private static boolean checkLengthPattern(int area, int city, int subscriber) {

        for (int[] length : VALID_TEL_LENGTH_PATTERNS) {
            if (area == length[0] && city == length[1] && subscriber == length[2]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 市外局番、市内局番、加入者番号の組が、有効な日本の電話番号であるかを精査する。<br>
     * 必須精査は当メソッドでは行わない。<br>
     * 全ての引数がnullまたは空文字の場合、{@code true}を返却する。<br>
     * 市外局番、市内局番、加入者番号のいずれかに空文字またはnullが含まれていることの精査は呼び出しもとにて行うこと。
     *
     * @param areaCode
     *            市外局番
     * @param cityCode
     *            市内局番
     * @param subscriberNumber
     *            加入者番号
     * @return 市外局番、市内局番、加入者番号の組が、有効な日本の電話番号である場合、{@code true}
     */
    public static boolean isValidJapaneseTelNum(String areaCode, String cityCode, String subscriberNumber) {

        if (StringUtil.isNullOrEmpty(areaCode, cityCode, subscriberNumber)) {
            return true;
        }

        if (areaCode == null || cityCode == null || subscriberNumber == null) {
            return false;
        }

        if (areaCode.startsWith("0")) {
            return checkLengthPattern(areaCode.length(), cityCode.length(), subscriberNumber.length());
        }

        return false;

    }

    /**
     * 郵便番号を精査する。<br>
     * 3桁数値 + "-" + 4桁数値、有効<br>
     * 7桁数値、有効<br>
     *
     * @param code
     *            郵便番号
     * @return 有効な郵便番号である場合、{@code true}
     */
    public static boolean isValidPostalCode(String code) {
        Pattern pattern1 = Pattern.compile(POSTALCODE_WITH_HYPHEN);
        Matcher matcher1 = pattern1.matcher(code);
        Pattern pattern2 = Pattern.compile(POSTALCODE);
        Matcher matcher2 = pattern2.matcher(code);
        if (matcher1.find() || matcher2.find()) {
            return true;
        }
        return false;
    }

    /**
     * 郵便番号を精査する。<br>
     * 3桁数値 + "-" + 4桁数値、有効<br>
     *
     * @param code
     *            郵便番号
     * @return 有効な郵便番号である場合、{@code true}
     */
    public static boolean isValidPostalCodeWithHyphen(String code) {
        Pattern pattern = Pattern.compile(POSTALCODE_WITH_HYPHEN);
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * パスワードを精査する。<br>
     * 半角の英字(A～Z,a～z)と数字(0～9)の両方を含む8文字以上<br>
     *
     * @param password
     *            パスワード
     * @return 有効なパスワードである場合、{@code true}
     */
    public static boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(VALID_PASSWORD);
        Matcher matcher = pattern.matcher(password);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * インタネットURLを精査する。
     *
     * @param internetUrl
     *            インタネットURL
     *
     * @return 有効なインタネットURLである場合、{@code true}
     */
    public static boolean isValidInternetUrl(String internetUrl) {
        Pattern pattern = Pattern.compile(INTERNET_URL);
        Matcher matcher = pattern.matcher(internetUrl);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

}
