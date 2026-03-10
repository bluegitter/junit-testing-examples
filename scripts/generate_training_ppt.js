const pptxgen = require('../.codex-ppt/node_modules/pptxgenjs');

const pptx = new pptxgen();
pptx.layout = 'LAYOUT_WIDE';
pptx.author = 'OpenAI Codex';
pptx.company = 'OpenAI';
pptx.subject = 'Java JUnit 单元测试培训';
pptx.title = 'Java JUnit 单元测试培训';
pptx.lang = 'zh-CN';
pptx.theme = {
  headFontFace: 'Georgia',
  bodyFontFace: 'Calibri',
  lang: 'zh-CN'
};
pptx.defineLayout({ name: 'CUSTOM', width: 13.333, height: 7.5 });
pptx.layout = 'CUSTOM';
pptx.theme = {
  headFontFace: 'Georgia',
  bodyFontFace: 'Calibri',
  lang: 'zh-CN'
};

const C = {
  navy: '1F3A5F',
  deep: '243447',
  cream: 'F4EFE7',
  sand: 'E7DCCB',
  terracotta: 'C65D3B',
  sage: '8FA38A',
  gold: 'D0A85C',
  ink: '2B2B2B',
  white: 'FFFFFF',
  mist: 'EEF3F6',
  coral: 'E07A5F'
};

function addHeader(slide, title, section, num, dark = false) {
  if (dark) {
    slide.background = { color: C.navy };
  } else {
    slide.background = { color: C.cream };
    slide.addShape(pptx.ShapeType.rect, { x: 0, y: 0, w: 0.38, h: 7.5, line: { color: C.terracotta, transparency: 100 }, fill: { color: C.terracotta } });
  }
  slide.addText(title, {
    x: 0.7, y: dark ? 0.62 : 0.42, w: 8.8, h: 0.7,
    fontFace: 'Georgia', fontSize: dark ? 26 : 24, bold: true,
    color: dark ? C.white : C.deep, margin: 0
  });
  slide.addText(section, {
    x: 10.9, y: 0.45, w: 1.5, h: 0.35, fontSize: 12, bold: true,
    color: dark ? C.sand : C.terracotta, align: 'right', margin: 0
  });
  slide.addShape(pptx.ShapeType.roundRect, {
    x: 12.45, y: 0.34, w: 0.55, h: 0.42,
    rectRadius: 0.08,
    line: { color: dark ? C.gold : C.deep, transparency: 100 },
    fill: { color: dark ? C.gold : C.deep }
  });
  slide.addText(String(num).padStart(2, '0'), {
    x: 12.45, y: 0.385, w: 0.55, h: 0.22, align: 'center',
    fontSize: 12, bold: true, color: C.white, margin: 0
  });
}

function addFooter(slide, text, dark = false) {
  slide.addText(text, {
    x: 0.72, y: 7.06, w: 11.3, h: 0.22,
    fontSize: 9.5, color: dark ? C.sand : '6D6D6D', italic: true, margin: 0
  });
}

function addBulletList(slide, items, opts = {}) {
  const runs = [];
  items.forEach((item, idx) => {
    runs.push({ text: item, options: { bullet: true, breakLine: idx !== items.length - 1 } });
  });
  slide.addText(runs, {
    x: opts.x, y: opts.y, w: opts.w, h: opts.h,
    fontSize: opts.fontSize || 16, color: opts.color || C.ink,
    breakLine: true, paraSpaceAfterPt: 10, valign: 'top', margin: 0.05,
    bullet: { indent: 14 }
  });
}

function addCard(slide, x, y, w, h, title, body, color, darkText = false) {
  slide.addShape(pptx.ShapeType.roundRect, {
    x, y, w, h, rectRadius: 0.08,
    line: { color, transparency: 100 },
    fill: { color },
    shadow: { type: 'outer', color: '000000', blur: 2, offset: 1, angle: 45, opacity: 0.08 }
  });
  slide.addText(title, {
    x: x + 0.18, y: y + 0.16, w: w - 0.36, h: 0.26,
    fontSize: 16, bold: true, color: darkText ? C.deep : C.white, margin: 0
  });
  slide.addText(body, {
    x: x + 0.18, y: y + 0.48, w: w - 0.36, h: h - 0.6,
    fontSize: 11.5, color: darkText ? C.ink : C.cream, margin: 0
  });
}

function addCodeBlock(slide, code, x, y, w, h, fontSize = 12) {
  slide.addShape(pptx.ShapeType.roundRect, {
    x, y, w, h, rectRadius: 0.05,
    line: { color: C.deep, transparency: 100 },
    fill: { color: C.deep }
  });
  slide.addText(code, {
    x: x + 0.16, y: y + 0.14, w: w - 0.32, h: h - 0.28,
    fontFace: 'Consolas', fontSize, color: C.cream,
    margin: 0, breakLine: true
  });
}

function addPhaseChip(slide, x, y, w, label, color) {
  slide.addShape(pptx.ShapeType.roundRect, {
    x, y, w, h: 0.38, rectRadius: 0.08,
    line: { color, transparency: 100 }, fill: { color }
  });
  slide.addText(label, { x, y: y + 0.08, w, h: 0.16, align: 'center', fontSize: 12, bold: true, color: C.white, margin: 0 });
}

// 1 Cover
{
  const slide = pptx.addSlide();
  slide.background = { color: C.navy };
  slide.addShape(pptx.ShapeType.rect, { x: 8.9, y: 0, w: 4.433, h: 7.5, line: { color: C.terracotta, transparency: 100 }, fill: { color: C.terracotta } });
  slide.addShape(pptx.ShapeType.rect, { x: 9.45, y: 0, w: 3.3, h: 7.5, line: { color: C.gold, transparency: 78 }, fill: { color: C.gold, transparency: 78 } });
  slide.addText('Java JUnit\n单元测试培训', {
    x: 0.8, y: 1.0, w: 6.6, h: 1.7, fontFace: 'Georgia',
    fontSize: 27, bold: true, color: C.white, margin: 0
  });
  slide.addText('Maven + JDK 1.8 + JUnit 5 + Mockito + Spring Boot + MyBatis', {
    x: 0.82, y: 2.95, w: 7.2, h: 0.35, fontSize: 16, color: C.sand, margin: 0
  });
  addCard(slide, 0.82, 4.0, 2.2, 1.35, '目标', '从基础写法到框架实战，建立可复用的测试方法论。', C.deep);
  addCard(slide, 3.18, 4.0, 2.2, 1.35, '范围', '覆盖纯单测、依赖隔离、切片测试、数据库测试与报告治理。', C.sage, true);
  addCard(slide, 5.54, 4.0, 2.2, 1.35, '方式', '以工程示例驱动讲解，每章都能回到代码直接演示。', C.gold, true);
  slide.addText('培训材料来源：docs/training-chapters + 示例工程', {
    x: 0.82, y: 6.72, w: 6.5, h: 0.22, fontSize: 10, color: C.sand, italic: true, margin: 0
  });
}

// 2 Why
{
  const slide = pptx.addSlide();
  addHeader(slide, '为什么团队要系统化做单元测试', '开场定位', 2);
  addCard(slide, 0.9, 1.2, 3.75, 1.55, '质量保障', '尽早暴露回归问题，把风险留在开发阶段，而不是线上。', C.terracotta);
  addCard(slide, 4.82, 1.2, 3.75, 1.55, '研发效率', '重构有依据，人工回归减少，改动更敢做。', C.sage, true);
  addCard(slide, 8.74, 1.2, 3.75, 1.55, '协作标准', '把业务规则写成可执行事实，Code Review 有共同语言。', C.deep);
  slide.addText('工程链路', { x: 0.92, y: 3.15, w: 1.2, h: 0.24, fontSize: 16, bold: true, color: C.deep, margin: 0 });
  const flow = ['需求理解', '编码实现', '单元测试', '本地验证', 'CI 执行', '上线发布'];
  flow.forEach((f, i) => {
    const x = 0.95 + i * 2.05;
    slide.addShape(pptx.ShapeType.roundRect, { x, y: 3.65, w: 1.55, h: 0.72, rectRadius: 0.05, line: { color: i === 2 ? C.terracotta : C.sand }, fill: { color: i === 2 ? C.terracotta : C.white } });
    slide.addText(f, { x, y: 3.93, w: 1.55, h: 0.18, align: 'center', fontSize: 12, bold: true, color: i === 2 ? C.white : C.deep, margin: 0 });
    if (i < flow.length - 1) {
      slide.addShape(pptx.ShapeType.chevron, { x: x + 1.63, y: 3.84, w: 0.28, h: 0.3, line: { color: C.gold, transparency: 100 }, fill: { color: C.gold } });
    }
  });
  addBulletList(slide, [
    '本次培训重点不是“会写一个 @Test”，而是“会持续写对测试”。',
    '讲解顺序从语言级基础，逐步进入服务层、框架层、数据库层和质量治理。',
    '所有内容都能回到项目中的真实示例类直接讲解。'
  ], { x: 0.95, y: 4.9, w: 11.6, h: 1.6, fontSize: 15 });
  addFooter(slide, '对应章节：01-培训目标.md');
}

// 3 Roadmap
{
  const slide = pptx.addSlide();
  addHeader(slide, '培训路线图：4 个阶段，14 个章节', '课程总览', 3);
  const phases = [
    ['阶段 A', '基础入门', '01-04', C.navy],
    ['阶段 B', 'JUnit 进阶', '05-08', C.terracotta],
    ['阶段 C', '框架实战', '09-11', C.sage],
    ['阶段 D', '治理落地', '12-14', C.gold]
  ];
  phases.forEach((p, i) => {
    const x = 0.9 + i * 3.15;
    addPhaseChip(slide, x, 1.08, 2.35, p[0] + ' · ' + p[2], p[3]);
    slide.addText(p[1], { x, y: 1.56, w: 2.35, h: 0.24, align: 'center', fontSize: 18, bold: true, color: C.deep, margin: 0 });
  });
  const boxes = [
    ['A', ['培训目标', '环境与项目结构', 'JUnit 核心写法', '生命周期与测试组织'], 0.9],
    ['B', ['参数化测试基础', '参数化测试进阶', 'Mock 与依赖隔离', '高级技巧'], 4.05],
    ['C', ['Spring Boot 测试分层', 'MyBatis Mapper CRUD', 'MyBatis Service 集成'], 7.2],
    ['D', ['最佳实践与反模式', '测试报告与覆盖率', 'Q&A 与落地建议'], 10.35]
  ];
  boxes.forEach((box, idx) => {
    slide.addShape(pptx.ShapeType.roundRect, {
      x: box[2], y: 2.1, w: 2.55, h: 3.75, rectRadius: 0.08,
      line: { color: idx === 0 ? C.navy : idx === 1 ? C.terracotta : idx === 2 ? C.sage : C.gold },
      fill: { color: C.white }
    });
    slide.addText(box[0], { x: box[2] + 0.18, y: 2.16, w: 0.28, h: 0.3, fontSize: 20, bold: true, color: idx === 0 ? C.navy : idx === 1 ? C.terracotta : idx === 2 ? C.sage : C.gold, margin: 0 });
    addBulletList(slide, box[1], { x: box[2] + 0.18, y: 2.62, w: 2.15, h: 2.8, fontSize: 13.5 });
  });
  addFooter(slide, '对应文件：TRAINING_OUTLINE.md');
}

// 4 Environment
{
  const slide = pptx.addSlide();
  addHeader(slide, '环境与项目结构：把测试跑稳定', '阶段 A', 4);
  addCard(slide, 0.95, 1.1, 3.0, 1.2, '基础环境', 'JDK 1.8\nMaven 项目组织\n标准 src/main / src/test 结构', C.navy);
  addCodeBlock(slide, 'mvn test\n\nmvn -Dtest=CalculatorTest test\n\nmvn clean verify', 0.95, 2.55, 3.0, 1.65);
  slide.addText('目录映射', { x: 4.45, y: 1.06, w: 1.4, h: 0.25, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  const dirs = [
    ['core', '纯 Java 业务逻辑，适合基础单测与参数化测试'],
    ['service', '依赖协作与 Mockito 示例'],
    ['spring', 'Spring Boot 与 MyBatis 场景'],
    ['test/resources', 'SQL、测试配置与初始化脚本']
  ];
  dirs.forEach((d, i) => {
    addCard(slide, 4.45 + (i % 2) * 4.0, 1.55 + Math.floor(i / 2) * 1.72, 3.55, 1.35, d[0], d[1], i % 2 === 0 ? C.sand : C.mist, true);
  });
  addBulletList(slide, [
    '环境问题很多不是测试代码本身，而是依赖版本、目录约定和构建命令没统一。',
    '本项目依赖 H2、Surefire、JaCoCo，适合本地和 CI 一致运行。'
  ], { x: 4.45, y: 5.25, w: 7.6, h: 1.1, fontSize: 14.5 });
  addFooter(slide, '对应章节：02-环境与项目结构.md');
}

// 5 JUnit core
{
  const slide = pptx.addSlide();
  addHeader(slide, 'JUnit 核心写法：AAA + 三类路径', '阶段 A', 5);
  addCodeBlock(slide, '// Arrange\nCalculator calculator = new Calculator();\n\n// Act\nint result = calculator.add(3, 4);\n\n// Assert\nassertEquals(7, result);', 0.95, 1.25, 4.15, 2.55);
  addCard(slide, 0.95, 4.1, 1.9, 1.25, '正常路径', '输入合法时，验证主业务逻辑。', C.terracotta);
  addCard(slide, 3.05, 4.1, 1.9, 1.25, '异常路径', '非法输入或状态不满足时，验证异常。', C.sage, true);
  addCard(slide, 5.15, 4.1, 1.9, 1.25, '边界路径', '0、null、空串、最大值、阈值附近。', C.deep);
  slide.addText('常用断言', { x: 7.45, y: 1.15, w: 1.5, h: 0.25, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  addCard(slide, 7.45, 1.55, 2.1, 1.1, 'assertEquals', '验证结果值与状态值。', C.white, true);
  addCard(slide, 9.75, 1.55, 2.1, 1.1, 'assertThrows', '验证异常类型与异常消息。', C.white, true);
  addCard(slide, 7.45, 2.9, 4.4, 1.1, 'assertTimeout', '验证特定逻辑是否超出合理执行时间。', C.white, true);
  addBulletList(slide, [
    '示例类：CalculatorTest、TimeoutAndDisabledTest。',
    '培训时建议按“正常 -> 异常 -> 边界”顺序讲，形成稳定思维模板。'
  ], { x: 7.45, y: 4.4, w: 4.6, h: 1.4, fontSize: 14 });
  addFooter(slide, '对应章节：03-JUnit核心写法.md');
}

// 6 lifecycle + basic param
{
  const slide = pptx.addSlide();
  addHeader(slide, '测试组织：生命周期与基础参数化', '阶段 A/B', 6);
  slide.addText('生命周期组织', { x: 0.95, y: 1.1, w: 2.0, h: 0.24, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  const steps = ['@BeforeEach', '@Test', '@AfterEach'];
  steps.forEach((s, i) => {
    const x = 0.95 + i * 1.95;
    slide.addShape(pptx.ShapeType.roundRect, { x, y: 1.6, w: 1.55, h: 0.8, rectRadius: 0.05, line: { color: C.navy }, fill: { color: i === 1 ? C.terracotta : C.white } });
    slide.addText(s, { x, y: 1.9, w: 1.55, h: 0.18, align: 'center', fontSize: 13, bold: true, color: i === 1 ? C.white : C.deep, margin: 0 });
    if (i < 2) slide.addShape(pptx.ShapeType.chevron, { x: x + 1.65, y: 1.87, w: 0.18, h: 0.22, line: { color: C.gold, transparency: 100 }, fill: { color: C.gold } });
  });
  addBulletList(slide, [
    '测试必须彼此独立，不能依赖执行顺序。',
    '@Nested 用于按业务场景分组，@DisplayName 用于提升报告可读性。'
  ], { x: 0.95, y: 2.75, w: 4.8, h: 1.3, fontSize: 14 });
  slide.addText('基础参数化', { x: 6.25, y: 1.1, w: 2.0, h: 0.24, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  addCodeBlock(slide, '@ParameterizedTest\n@CsvSource({" TOM ,Tom", "jErrY,Jerry"})\nvoid shouldNormalize(String raw, String expected) {\n  assertEquals(expected, normalizer.normalizeName(raw));\n}', 6.25, 1.55, 5.95, 1.9);
  addCard(slide, 6.25, 3.8, 1.7, 1.2, '@CsvSource', '输入与期望值成对出现', C.sage, true);
  addCard(slide, 8.2, 3.8, 1.7, 1.2, '@ValueSource', '单值批量验证', C.sand, true);
  addCard(slide, 10.15, 3.8, 1.7, 1.2, '@NullSource', 'null 边界输入', C.deep);
  addBulletList(slide, [
    '适合：同一规则、多组数据。',
    '不适合：每组数据背后其实是完全不同的测试意图。'
  ], { x: 6.25, y: 5.35, w: 5.9, h: 1.0, fontSize: 14 });
  addFooter(slide, '对应章节：04-生命周期与测试组织.md，05-参数化测试基础.md');
}

// 7 advanced param
{
  const slide = pptx.addSlide();
  addHeader(slide, '参数化测试进阶：把数据建模成业务事实', '阶段 B', 7);
  slide.addText('@MethodSource 的价值', { x: 0.95, y: 1.08, w: 2.2, h: 0.25, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  addBulletList(slide, [
    '多参数场景下，可读性比 @CsvSource 更强。',
    '测试数据可以分组、复用，并逐步演进。',
    '每组 Arguments 应完整表达一个业务事实。'
  ], { x: 0.95, y: 1.5, w: 4.5, h: 1.8, fontSize: 14.5 });
  addCodeBlock(slide, 'static Stream<Arguments> validCases() {\n  return Stream.of(\n    arguments(100.0, "VIP", 80.0),\n    arguments(200.0, "NORMAL", 200.0)\n  );\n}', 0.95, 3.65, 4.5, 2.05);
  slide.addText('示例拆分策略', { x: 5.95, y: 1.08, w: 2.0, h: 0.25, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  addCard(slide, 5.95, 1.52, 2.65, 1.55, '合法输入', '验证不同等级、不同金额下的最终价格。', C.terracotta);
  addCard(slide, 8.88, 1.52, 2.65, 1.55, '非法输入', '空等级、未知等级、越界输入应触发异常。', C.sage, true);
  addCard(slide, 5.95, 3.45, 5.58, 1.35, '关键判断', '优先让 MethodSource 反映业务分类，而不是把所有数据混在一个大表里。', C.white, true);
  addBulletList(slide, [
    '示例类：DiscountCalculatorMethodSourceTest。',
    '培训讲解重点：每组参数不是“数据点”，而是一条业务规则。'
  ], { x: 5.95, y: 5.15, w: 5.7, h: 1.0, fontSize: 14 });
  addFooter(slide, '对应章节：06-参数化测试进阶.md');
}

// 8 mock essentials
{
  const slide = pptx.addSlide();
  addHeader(slide, 'Mock 与依赖隔离：先把服务层讲透', '阶段 B', 8);
  addCard(slide, 0.95, 1.15, 2.2, 1.2, '@Mock', '替换仓储、邮件、HTTP 等外部依赖。', C.navy);
  addCard(slide, 3.35, 1.15, 2.2, 1.2, '@InjectMocks', '把 mock 注入被测服务，聚焦当前类职责。', C.terracotta);
  addCard(slide, 5.75, 1.15, 2.2, 1.2, 'when/then', '控制依赖返回、异常或动态响应。', C.sage, true);
  addCard(slide, 8.15, 1.15, 2.2, 1.2, 'verify', '验证副作用、交互次数与顺序。', C.gold, true);
  addCodeBlock(slide, 'when(userRepository\n    .findByEmail("new@company.com"))\n    .thenReturn(null);\nverify(emailSender)\n    .sendWelcomeEmail("new@company.com");', 0.95, 2.75, 5.15, 1.85, 11);
  addBulletList(slide, [
    'ArgumentCaptor：校验传入 save(...) 的对象字段。',
    'InOrder：校验查重 -> 保存 -> 发邮件的调用顺序。',
    'verifyNoMoreInteractions：限制额外副作用。'
  ], { x: 6.45, y: 2.85, w: 5.4, h: 1.8, fontSize: 14.5 });
  slide.addShape(pptx.ShapeType.roundRect, { x: 0.95, y: 5.05, w: 10.9, h: 1.15, rectRadius: 0.05, line: { color: C.sand }, fill: { color: C.white } });
  slide.addText('判断原则：什么该 mock？数据库、消息、HTTP、邮件等外部依赖该 mock；值对象、纯工具类、被测类本身通常不该 mock。', {
    x: 1.12, y: 5.34, w: 10.55, h: 0.46, fontSize: 14.2, color: C.deep, margin: 0
  });
  addFooter(slide, '对应章节：07-Mock与依赖隔离.md（UserService 三个测试类）');
}

// 9 external api mock
{
  const slide = pptx.addSlide();
  addHeader(slide, '外部接口 Mock：从单次调用到批量聚合', '阶段 B', 9);
  const scenarios = [
    ['基础', '正常返回 / 超时 / 不可用', C.navy, false],
    ['分类', '连接异常 / 业务错误响应 / 运行时异常', C.terracotta, false],
    ['容错', '空响应 / 未知状态 / 输入校验', C.sage, true],
    ['聚合', '批量查询下的部分成功部分失败', C.gold, true]
  ];
  scenarios.forEach((s, i) => addCard(slide, 0.95 + (i % 2) * 5.9, 1.25 + Math.floor(i / 2) * 1.72, 5.1, 1.35, s[0], s[1], s[2], s[3]));
  addCodeBlock(slide, 'when(externalOrderClient.queryStatus("ORD-1006"))\n    .thenThrow(new SocketTimeoutException("Read timed out"))\n    .thenReturn(new OrderStatusResponse("ORD-1006", "PAID"));', 0.95, 4.95, 5.75, 1.45);
  addBulletList(slide, [
    'thenThrow(...).thenReturn(...) 适合讲“瞬时失败重试”。',
    '批量查询要验证：单项失败不拖垮整批、超时项重试次数、非法输入不触发远端调用。',
    '示例类：OrderStatusServiceExternalCallTest。'
  ], { x: 7.0, y: 4.95, w: 5.2, h: 1.45, fontSize: 13.8 });
  addFooter(slide, '对应章节：07-Mock与依赖隔离.md（外部接口基础 + 进阶场景）');
}

// 10 advanced junit
{
  const slide = pptx.addSlide();
  addHeader(slide, '高级技巧：表达力增强，不是为了“更炫”', '阶段 B', 10);
  addCard(slide, 0.95, 1.2, 2.4, 1.15, 'assertAll', '一个业务动作下聚合多个结果断言。', C.terracotta);
  addCard(slide, 3.6, 1.2, 2.4, 1.15, 'assertDoesNotThrow', '显式声明合法输入下不应抛错。', C.sage, true);
  addCard(slide, 6.25, 1.2, 2.4, 1.15, 'assumeTrue', '执行前提不满足时跳过。', C.gold, true);
  addCard(slide, 8.9, 1.2, 2.4, 1.15, '@RepeatedTest / @TestFactory', '重复执行与动态生成测试。', C.deep);
  slide.addShape(pptx.ShapeType.roundRect, { x: 0.95, y: 2.8, w: 6.05, h: 2.55, rectRadius: 0.06, line: { color: C.sand }, fill: { color: C.white } });
  slide.addText('使用边界', { x: 1.15, y: 3.0, w: 1.4, h: 0.22, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  addBulletList(slide, [
    'assertAll 只适合“同一业务上下文”的多个断言。',
    'assumeTrue 不能用来掩盖真实失败。',
    '@RepeatedTest 不能当作不稳定代码的补丁。',
    '@TestFactory 不是参数化测试的升级版。'
  ], { x: 1.15, y: 3.38, w: 5.55, h: 1.55, fontSize: 14.2 });
  addCard(slide, 7.35, 2.8, 5.0, 2.55, '@TestFactory 的额外提醒', '更适合轻量、纯逻辑的动态用例。若场景高度依赖固定数据、生命周期控制或初始化/清理逻辑，优先考虑 @ParameterizedTest。', C.mist, true);
  addFooter(slide, '对应章节：08-高级技巧.md');
}

// 11 spring layers
{
  const slide = pptx.addSlide();
  addHeader(slide, 'Spring Boot 测试分层：以最小范围换最大信心', '阶段 C', 11);
  const cols = [
    ['纯单元测试', '不启 Spring\n执行最快\n只测当前类业务逻辑', C.navy],
    ['切片测试', '@WebMvcTest / @MybatisTest\n只加载某一层', C.terracotta],
    ['全上下文测试', '@SpringBootTest\n验证跨层协作与配置装配', C.sage]
  ];
  cols.forEach((c, i) => {
    addCard(slide, 1.0 + i * 4.1, 1.45, 3.25, 2.15, c[0], c[1], c[2], i === 2);
  });
  slide.addText('选择准则', { x: 1.0, y: 4.15, w: 1.5, h: 0.22, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  addBulletList(slide, [
    '只关心业务逻辑：优先纯单元测试。',
    '只关心某一层框架行为：用切片测试。',
    '关心多层协作和自动装配：再用全上下文测试。'
  ], { x: 1.0, y: 4.5, w: 5.3, h: 1.25, fontSize: 14.5 });
  addCodeBlock(slide, '@WebMvcTest(GreetingController.class)\nclass GreetingControllerWebMvcTest { ... }\n\n@SpringBootTest\nclass EmployeeServiceSpringBootTest { ... }', 7.05, 4.1, 5.3, 1.65);
  addFooter(slide, '对应章节：09-SpringBoot测试分层.md');
}

// 12 mybatis mapper
{
  const slide = pptx.addSlide();
  addHeader(slide, 'MyBatis Mapper 层：把 CRUD 验证讲完整', '阶段 C', 12);
  const crud = [
    ['insert', '影响行数 + 主键回填'],
    ['selectById', '单条查询结果映射 + 不存在记录'],
    ['update', '更新后回查，验证最终状态'],
    ['delete', '删除后查询不可见'],
    ['selectAll', '数量与排序都要断言']
  ];
  crud.forEach((item, i) => {
    const x = 0.95 + (i % 3) * 4.0;
    const y = 1.4 + Math.floor(i / 3) * 1.7;
    addCard(slide, x, y, 3.35, 1.25, item[0], item[1], i % 2 === 0 ? C.white : C.mist, true);
  });
  addCodeBlock(slide, '@MybatisTest\n@Sql(scripts = "/sql/employee-schema.sql",\n     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)', 0.95, 4.95, 5.2, 1.35);
  addBulletList(slide, [
    'H2 内存库让本地、CI、培训环境保持一致。',
    '不要只断言影响行数，写入类操作建议配合“写后再读”。',
    '示例：EmployeeMapperMybatisTest。'
  ], { x: 6.55, y: 4.9, w: 5.55, h: 1.4, fontSize: 14 });
  addFooter(slide, '对应章节：10-MyBatisMapper层CRUD测试.md');
}

// 13 integration
{
  const slide = pptx.addSlide();
  addHeader(slide, 'Service 集成测试：验证完整调用链', '阶段 C', 13);
  slide.addText('EmployeeService 集成测试链路', { x: 0.95, y: 1.08, w: 2.9, h: 0.24, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  const chain = ['Spring 容器', 'EmployeeService', 'EmployeeMapper', 'H2 / @Sql'];
  chain.forEach((c, i) => {
    const x = 0.98 + i * 2.9;
    slide.addShape(pptx.ShapeType.roundRect, { x, y: 1.65, w: 2.1, h: 0.85, rectRadius: 0.05, line: { color: i === 1 ? C.terracotta : C.sand }, fill: { color: i === 1 ? C.terracotta : C.white } });
    slide.addText(c, { x, y: 1.98, w: 2.1, h: 0.18, align: 'center', fontSize: 13.5, bold: true, color: i === 1 ? C.white : C.deep, margin: 0 });
    if (i < chain.length - 1) slide.addShape(pptx.ShapeType.chevron, { x: x + 2.2, y: 1.98, w: 0.22, h: 0.22, line: { color: C.gold, transparency: 100 }, fill: { color: C.gold } });
  });
  addCard(slide, 0.95, 3.25, 3.8, 1.55, '为什么这里不再 Mock', '目标不是只验证某个方法返回值，而是验证 Bean 注入、Mapper 协作和真实数据访问链路。', C.navy);
  addCard(slide, 4.95, 3.25, 3.8, 1.55, '@Sql 的价值', '每个测试前重建数据环境，避免历史数据污染。', C.sage, true);
  addCard(slide, 8.95, 3.25, 3.35, 1.55, '职责边界', 'Mapper 测试看 SQL，Service 集成测试看跨层协作。', C.gold, true);
  addBulletList(slide, [
    '适合讲：什么时候 @SpringBootTest 必要，什么时候它过重。',
    '示例：EmployeeServiceSpringBootTest。'
  ], { x: 0.95, y: 5.25, w: 11.1, h: 0.95, fontSize: 14.2 });
  addFooter(slide, '对应章节：11-MyBatisService集成测试.md');
}

// 14 best practices
{
  const slide = pptx.addSlide();
  addHeader(slide, '最佳实践：先设计可测试代码，再写测试', '阶段 D', 14);
  addCard(slide, 0.95, 1.25, 5.5, 3.05, '推荐做法', '1. 一个测试只验证一个明确行为\n2. 命名体现业务语义\n3. 测试数据最小化且可读\n4. 断言聚焦业务结果\n5. 依赖通过构造器注入\n6. 将业务规则与框架代码分离', C.white, true);
  addCard(slide, 6.8, 1.25, 5.55, 3.05, '反模式', '1. 在测试里复制业务实现逻辑\n2. 过度 Mock\n3. 依赖时间、随机数、网络等不稳定因素\n4. 长期忽视失败或跳过测试\n5. 为了覆盖率数字写低价值测试', C.deep);
  addCard(slide, 0.95, 4.6, 3.55, 1.1, '可测试性', '先设计依赖边界，再进入测试阶段。', C.sage, true);
  addCard(slide, 4.72, 4.6, 3.55, 1.1, '评审重点', '场景覆盖、断言有效性、是否过度耦合实现细节。', C.gold, true);
  addCard(slide, 8.49, 4.6, 3.86, 1.1, '项目映射', 'UserService、DiscountCalculator\nOrderStatusService', C.terracotta);
  addFooter(slide, '对应章节：12-最佳实践与反模式.md');
}

// 15 reports
{
  const slide = pptx.addSlide();
  addHeader(slide, '报告与覆盖率：把测试结果变成治理依据', '阶段 D', 15);
  addCodeBlock(slide, 'mvn clean verify\n\ntarget/surefire-reports/\ntarget/site/surefire-report.html\ntarget/site/jacoco/index.html', 0.95, 1.3, 4.3, 2.2);
  addCard(slide, 5.7, 1.3, 2.95, 1.3, 'Surefire', '回答：测试有没有跑、谁失败了、谁被跳过了。', C.navy);
  addCard(slide, 8.95, 1.3, 3.0, 1.3, 'JaCoCo', '回答：哪些代码被执行到，哪些分支仍是盲区。', C.sage, true);
  slide.addText('读报告的顺序', { x: 5.7, y: 3.1, w: 1.8, h: 0.24, fontSize: 17, bold: true, color: C.deep, margin: 0 });
  addBulletList(slide, [
    '先看 Failures / Errors 是否为 0。',
    '再看 Skipped 是否合理。',
    '再看关键类和刚修改过类的分支覆盖率。',
    '覆盖率高不等于测试质量高。'
  ], { x: 5.7, y: 3.48, w: 6.25, h: 1.6, fontSize: 14.2 });
  addCard(slide, 0.95, 4.05, 4.3, 1.6, '当前项目可讲解点', '新增 OrderStatusServiceExternalCallTest 后，可观察异常分支覆盖率如何提升。', C.gold, true);
  addCard(slide, 0.95, 5.9, 10.95, 0.65, 'CI 建议：固定执行 mvn clean verify，收集 Surefire XML 与 jacoco.xml，并对关键模块设置最低阈值。', '', C.white, true);
  addFooter(slide, '对应章节：13-测试报告与覆盖率报告生成.md');
}

// 16 rollout + close
{
  const slide = pptx.addSlide();
  addHeader(slide, '落地建议：从培训走向团队默认实践', '收束', 16, true);
  const steps = [
    ['01', '统一最低标准', '新增核心逻辑必须带测试，失败测试不得合并。'],
    ['02', '纳入 Code Review', '不只看有没有测试，还要看场景覆盖和断言质量。'],
    ['03', '接入 CI 与报告', '让测试从个人习惯变成工程机制。'],
    ['04', '建立持续改进机制', '优先改高风险模块，逐步提高基线。']
  ];
  steps.forEach((s, i) => {
    const x = 0.92 + (i % 2) * 6.0;
    const y = 1.35 + Math.floor(i / 2) * 2.2;
    slide.addShape(pptx.ShapeType.roundRect, { x, y, w: 5.35, h: 1.55, rectRadius: 0.07, line: { color: i % 2 === 0 ? C.terracotta : C.gold, transparency: 100 }, fill: { color: i % 2 === 0 ? C.terracotta : C.gold } });
    slide.addText(s[0], { x: x + 0.2, y: y + 0.18, w: 0.55, h: 0.3, fontSize: 19, bold: true, color: C.white, margin: 0 });
    slide.addText(s[1], { x: x + 0.9, y: y + 0.22, w: 4.15, h: 0.24, fontSize: 16, bold: true, color: C.white, margin: 0 });
    slide.addText(s[2], { x: x + 0.9, y: y + 0.58, w: 4.15, h: 0.55, fontSize: 12.2, color: C.cream, margin: 0 });
  });
  slide.addText('结论：培训的终点不是“听懂”，而是让团队形成可执行、可评审、可持续的测试习惯。', {
    x: 0.95, y: 6.55, w: 11.6, h: 0.32, fontSize: 16, bold: true, color: C.sand, margin: 0
  });
  addFooter(slide, '对应章节：14-QA与落地建议.md', true);
}

pptx.writeFile({ fileName: 'deliverables/java-junit-training-deck.pptx' });
