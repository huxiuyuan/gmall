package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    // 别忘了创建D:\\project\rsa目录
	private static final String pubKeyPath = "F:\\ideaFile\\gmall\\rsa\\rsa.pub";
    private static final String priKeyPath = "F:\\ideaFile\\gmall\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

//     @BeforeEach
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE3MjE2NTc5Njl9.VZf-oIjccFAG_kgHCi7ml5Lbe5kDdN2NUf0T7pOf1Xfzu2nERJMMvIJ4J1F1riHX2eAFvdveF5RhXidpYO-rXMy0ecAwaCHoTQD3gOHy00zrimJU5ETnpHAtQC9HAgGm-DfHzROgYKTpIgX05GAOqECl_evIEZS5QwoCuhszfgqPDb_oS0SOAkqWAUINIjNK8EBwfJu9vdxgL0CMppwmLnUO8aXYNforX_oRhphIwvmldWIMB6gDyJDHwl6bwysWi1Dw4HkGkTTRzAZPooy-gWAWNi5CPj2qPWzTSORXXgd3XHEsapt0hrne-no30sPCmVcC60FlmCToDJCLVEyklQ";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}