package com.codyzeng.esample.user;

import com.codyzeng.esample.user.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/6
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIndexTest {

    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    @Test
    @DisplayName("删除索引")
    @Order(1)
    void deleteUserIndex() {
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(User.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
    }

    @Test
    @DisplayName("创建索引")
    @Order(2)
    void createUserIndex() {
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(User.class);

        Map<String, Object> settings = new HashMap<>();
        Map<String, Object> index = new HashMap<>();
        settings.put("index", index);
        index.put("number_of_shards", 3);
        index.put("number_of_replicas", 1);

        Document mapping = indexOperations.createMapping(User.class);
        indexOperations.create(settings, mapping);
    }


    @Test
    @DisplayName("批量插入或更新文档")
    @Order(3)
    void bulkCreateUser() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .id(1L)
                .username("海月")
                .age(25)
                .province("上海")
                .city("上海")
                .district("闵行区")
                .address("上海市闵行区龙茗路与宜山路交叉路口往西南约200米-大上海国际花园")
                .location(new GeoPoint(31.169939, 121.38484))
                .about("海月身后有三个月盘，释放【瞬华】、【 流光】时会消耗一个月盘，全部消耗后【瞬华】、 【流光】会进入完整的技能冷却，同时获得次强化普攻， 额外产生3颗法球，每颗法球造 成25(+8%法术攻击)法术伤害。【瞬华】、【 流光】命中目标后会给目标叠加一层印记，再 次命中后会触发不同的效果。")
                .build());

        users.add(User.builder()
                .id(2L)
                .username("白起")
                .age(42)
                .province("上海")
                .city("上海")
                .district("闵行区")
                .address("上海市闵行区富强街七宝三中西北侧约100米-三佳花苑")
                .location(new GeoPoint(31.161372, 121.367726))
                .about("白起造成或受到伤害时会获得暗影之力，提升移动速度和治疗效果，最多可以提升70点移动速度和80%的治疗效果。")
                .build());

        users.add(User.builder()
                .id(3L)
                .username("戈娅")
                .age(31)
                .province("上海")
                .city("上海")
                .district("闵行区")
                .address("上海市闵行区星中路1111弄-阳光乾城苑")
                .location(new GeoPoint(31.161, 121.37734))
                .about("戈娅驾驶沙舟作战，可以在移动中攻击，轻点一次普攻会用手弩快速连续射击3次，并使沙舟刹车进入漂移状态，每次造成20（+20%物理攻击）物理伤害。连续射击6次会追加一次强力射击，造成50（+100%物理攻击）物理伤害，同时沙舟会加速起步。每次箭矢附带的发球效果制造成35%伤害，长按普攻也可持续射出箭矢。")
                .build());

        users.add(User.builder()
                .id(4L)
                .username("姜子牙")
                .age(78)
                .province("上海")
                .city("上海")
                .district("徐汇区")
                .address("上海市徐汇区古宜路与宜山路交叉路口往西约150米-宜德苑")
                .location(new GeoPoint(31.19179, 121.432682))
                .about("若姜子牙对目标造成伤害后目标在3秒内死亡，目标会为自己提供1~3个经验光球，可提升自身经验。 姜子牙到达15级时会恢复封神之力，获得一次性技能【赐封神力】，并突破等级上限至25级，每次升级可获得法术攻击。 使用【赐封神力】选定一名队友对其封神，灌注经验，并使其也突破等级上限，且有概率获取经验光球")
                .build());

        users.add(User.builder()
                .id(5L)
                .username("狄仁杰")
                .age(39)
                .province("上海")
                .city("上海")
                .district("长宁区")
                .address("上海市长宁区汇川路88弄-凯欣豪园")
                .location(new GeoPoint(31.22267, 121.424331))
                .about("狄仁杰向指定方向甩出六道令牌，对命中的目标造成165/185/205/225/245/265(+22％物理加成)点物理伤害和110/130/150/170/190/210点法术伤害，其中蓝牌只能造成50％的伤害，但蓝牌会减少目标30％移动速度，持续1秒，当同一目标受到多道令牌伤害，从第二道令牌开始伤害会衰减至初始伤害的30％，令牌可以触发普通攻击的法球效果")
                .build());

        users.add(User.builder()
                .id(6L)
                .username("赵怀真")
                .age(37)
                .province("上海")
                .city("上海")
                .district("静安区")
                .address("上海市静安区新闸路831号-丽都新贵")
                .location(new GeoPoint(31.240785, 121.466356))
                .about("怀真摆出招架姿态持续叠加护盾并回复自身生命。若受到硬控将反击造成伤害和击退并回复与出招时相同生命。结束招架姿态时对周围造成伤害和减速效果")
                .build());

        users.add(User.builder()
                .id(7L)
                .username("大乔")
                .age(22)
                .province("上海")
                .city("上海")
                .district("闵行区")
                .address("上海市闵行区红松东路上海虹桥祥源希尔顿酒店东南侧约130米-古北壹号")
                .location(new GeoPoint(31.192432, 121.40251))
                .about("大乔与附近600范围内最近的队友将会增加40～60移动速度，增加幅度随英雄等级成长")
                .build());

        users.add(User.builder()
                .id(8L)
                .username("妲己")
                .age(18)
                .province("上海")
                .city("上海")
                .district("浦东新区")
                .address("上海市浦东新区花园石桥路28弄1-8号-汤臣一品")
                .location(new GeoPoint(31.238794, 121.508506))
                .about("妲己技能命中敌人会减少目标30~72点法术防御，持续3秒，最多叠加3层")
                .build());

        elasticsearchTemplate.save(users);
    }

    @Test
    @DisplayName("插入或更新单个文档")
    @Order(4)
    void createUser() {
        elasticsearchTemplate.save(User.builder()
                .id(9L)
                .username("老六")
                .age(33)
                .province("上海")
                .city("上海")
                .district("浦东新区")
                .address("上海市浦东新区花园石桥路28弄1-8号-汤臣一品")
                .location(new GeoPoint(31.238794, 121.508506))
                .about("槟榔妹真好玩")
                .build());
    }

    @Test
    @DisplayName("获取单个文档")
    @Order(5)
    void getUser() {
        User user = elasticsearchTemplate.get("9",User.class);
        assert user != null;
        Assertions.assertEquals("老六",user.getUsername());
    }
}
