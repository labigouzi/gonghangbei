from pathlib import Path
from datetime import date
from docx import Document
from docx.shared import Cm, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.enum.section import WD_SECTION
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.enum.style import WD_STYLE_TYPE
from docx.enum.text import WD_BREAK
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch


ROOT = Path(__file__).resolve().parent
FIG = ROOT / "figures"
FIG.mkdir(exist_ok=True)
DOCX = ROOT / "忆路银龄项目书.docx"
MD = ROOT / "忆路银龄项目书.md"

TITLE = "忆路银龄"
SUBTITLE = "基于多智能体协同与RAG知识增强的银龄养老金融陪伴与风险防护平台"


def set_font(run, name="宋体", size=12, bold=False, color=None):
    run.font.name = name
    run._element.get_or_add_rPr().rFonts.set(qn("w:eastAsia"), name)
    run.font.size = Pt(size)
    run.bold = bold
    if color:
        run.font.color.rgb = RGBColor(*color)


def shade(cell, fill):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), fill)
    tc_pr.append(shd)


def set_cell_text(cell, text, bold=False, color=None, size=9.5):
    cell.text = ""
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(0)
    r = p.add_run(str(text))
    set_font(r, "微软雅黑", size, bold, color)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER


def add_table(doc, headers, rows, widths=None):
    table = doc.add_table(rows=1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.style = "Table Grid"
    for i, h in enumerate(headers):
        shade(table.rows[0].cells[i], "1F4E5F")
        set_cell_text(table.rows[0].cells[i], h, True, (255, 255, 255), 9)
    for ri, row in enumerate(rows):
        cells = table.add_row().cells
        for i, value in enumerate(row):
            if ri % 2:
                shade(cells[i], "EEF5F3")
            set_cell_text(cells[i], value, False, None, 8.8)
    if widths:
        for row in table.rows:
            for i, width in enumerate(widths):
                row.cells[i].width = Cm(width)
    doc.add_paragraph()
    return table


def add_heading(doc, text, level=1):
    p = doc.add_paragraph(style=f"Heading {level}")
    p.paragraph_format.keep_with_next = True
    r = p.add_run(text)
    return p


def add_para(doc, text, bold_prefix=None):
    p = doc.add_paragraph()
    p.paragraph_format.first_line_indent = Cm(0.74)
    p.paragraph_format.line_spacing = 1.45
    p.paragraph_format.space_after = Pt(6)
    if bold_prefix and text.startswith(bold_prefix):
        r = p.add_run(bold_prefix)
        set_font(r, "宋体", 11, True)
        text = text[len(bold_prefix):]
    r = p.add_run(text)
    set_font(r, "宋体", 11)
    return p


def add_bullets(doc, items):
    for item in items:
        p = doc.add_paragraph(style="List Bullet")
        p.paragraph_format.left_indent = Cm(0.8)
        p.paragraph_format.space_after = Pt(3)
        r = p.add_run(item)
        set_font(r, "宋体", 10.5)


def caption(doc, text):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run(text)
    set_font(r, "宋体", 9, False, (90, 90, 90))


def page_break(doc):
    # Let Word paginate naturally after the cover. Explicit breaks near large
    # tables can otherwise create blank pages when a table already fills a page.
    return None


def draw_flow(path):
    plt.rcParams["font.sans-serif"] = ["Microsoft YaHei", "SimHei"]
    plt.rcParams["axes.unicode_minus"] = False
    fig, ax = plt.subplots(figsize=(12, 4.8))
    ax.set_xlim(0, 12); ax.set_ylim(0, 5); ax.axis("off")
    items = [
        (0.3, 3.15, "需求识别", "Intent"), (2.25, 3.15, "用户画像", "Profile"),
        (4.2, 3.15, "风险评估", "Rules"), (6.15, 3.15, "资金规划", "Planning"),
        (8.1, 3.15, "知识检索", "RAG"), (10.05, 3.15, "报告生成", "LLM"),
        (2.25, 0.8, "诈骗防护", "Text / OCR"), (5.15, 0.8, "金融健康", "Score"),
        (8.05, 0.8, "家庭协同", "Family")]
    for x, y, t, s in items:
        color = "#1F6B62" if y > 2 else "#C17C38"
        box = FancyBboxPatch((x, y), 1.55, .92, boxstyle="round,pad=.04,rounding_size=.08",
                             facecolor=color, edgecolor="none")
        ax.add_patch(box)
        ax.text(x+.775, y+.58, t, ha="center", va="center", color="white", fontsize=12, weight="bold")
        ax.text(x+.775, y+.25, s, ha="center", va="center", color="#E8F2EF", fontsize=8)
    for i in range(5):
        ax.add_patch(FancyArrowPatch((1.87+i*1.95,3.61),(2.18+i*1.95,3.61),arrowstyle="-|>",mutation_scale=14,color="#52727A"))
    ax.add_patch(FancyArrowPatch((10.82,3.05),(8.85,1.82),connectionstyle="arc3,rad=-.12",arrowstyle="-|>",mutation_scale=14,color="#52727A"))
    ax.add_patch(FancyArrowPatch((3.82,1.26),(5.08,1.26),arrowstyle="-|>",mutation_scale=14,color="#52727A"))
    ax.add_patch(FancyArrowPatch((6.72,1.26),(8.0,1.26),arrowstyle="-|>",mutation_scale=14,color="#52727A"))
    ax.text(6, 4.65, "养老金融智能决策与陪伴闭环", ha="center", fontsize=17, weight="bold", color="#183A43")
    fig.tight_layout()
    fig.savefig(path, dpi=200, bbox_inches="tight", facecolor="white")
    plt.close(fig)


def draw_arch(path):
    plt.rcParams["font.sans-serif"] = ["Microsoft YaHei", "SimHei"]
    fig, ax = plt.subplots(figsize=(11, 7))
    ax.set_xlim(0, 11); ax.set_ylim(0, 7); ax.axis("off")
    layers = [
        (5.75, "交互层", ["Vue 3 / Element Plus", "适老化页面", "Agent链与健康评分可视化"], "#375A7F"),
        (4.55, "接口与安全层", ["RESTful API", "JWT / RBAC", "统一响应与异常处理"], "#467B75"),
        (3.35, "业务与智能体层", ["养老金融规划", "六节点Agent编排", "诈骗检测 / 家庭协同"], "#A66A34"),
        (2.15, "AI与知识层", ["DeepSeek / Qwen / Mock", "Embedding + RAG", "pgvector + 来源展示"], "#6B5B7E"),
        (0.95, "数据与基础设施层", ["MySQL / Redis", "PostgreSQL pgvector", "MinIO / Docker Compose"], "#53636B")]
    for y, label, items, color in layers:
        ax.add_patch(FancyBboxPatch((.4,y),10.2,.85,boxstyle="round,pad=.03,rounding_size=.06",facecolor="#F5F7F7",edgecolor=color,linewidth=1.5))
        ax.add_patch(FancyBboxPatch((.4,y),1.7,.85,boxstyle="round,pad=.03,rounding_size=.06",facecolor=color,edgecolor=color))
        ax.text(1.25,y+.425,label,ha="center",va="center",color="white",weight="bold",fontsize=11)
        for i,t in enumerate(items):
            ax.text(3.1+i*2.5,y+.425,t,ha="center",va="center",fontsize=9.4,color="#26353A")
    ax.text(5.5,6.7,"忆路银龄分层技术架构",ha="center",fontsize=18,weight="bold",color="#183A43")
    ax.text(5.5,.35,"dev：真实基础设施与模型能力  |  demo：无外部依赖、本地降级",ha="center",fontsize=10.5,color="#A05035")
    fig.tight_layout(); fig.savefig(path,dpi=200,bbox_inches="tight",facecolor="white"); plt.close(fig)


def draw_agent(path):
    plt.rcParams["font.sans-serif"] = ["Microsoft YaHei", "SimHei"]
    fig, ax = plt.subplots(figsize=(12, 4.8))
    ax.set_xlim(0,12); ax.set_ylim(0,4.8); ax.axis("off")
    names=["意图识别\nAgent","用户画像\nAgent","风险评估\nAgent","资金规划\nAgent","产品匹配\nAgent","报告生成\nAgent"]
    subs=["分类+置信度","参数补齐+标签","0—100规则分","20/60/20用途","模拟类别匹配","RAG+LLM表达"]
    for i,(n,s) in enumerate(zip(names,subs)):
        x=.25+i*1.95
        ax.add_patch(FancyBboxPatch((x,2.15),1.55,1.15,boxstyle="round,pad=.05,rounding_size=.12",facecolor="#E8F2EF",edgecolor="#1F6B62",linewidth=1.8))
        ax.text(x+.775,2.82,n,ha="center",va="center",fontsize=10.5,weight="bold",color="#174C46")
        ax.text(x+.775,2.35,s,ha="center",va="center",fontsize=7.7,color="#53636B")
        if i<5: ax.add_patch(FancyArrowPatch((x+1.58,2.72),(x+1.9,2.72),arrowstyle="-|>",mutation_scale=14,color="#C17C38"))
    ax.text(6,4.3,"可观测、可测试、可替换的六节点金融智能体",ha="center",fontsize=17,weight="bold",color="#183A43")
    ax.text(6,.9,"每个节点输出结构化中间结果；规则负责底线，知识库负责依据，LLM负责适老化表达",ha="center",fontsize=11,color="#4B5960")
    fig.tight_layout(); fig.savefig(path,dpi=200,bbox_inches="tight",facecolor="white"); plt.close(fig)


def draw_score(path):
    plt.rcParams["font.sans-serif"] = ["Microsoft YaHei", "SimHei"]
    labels=["资产安全","养老准备","风险控制","医疗保障"]
    values=[85,80,78,69]
    fig,ax=plt.subplots(figsize=(8,4.2))
    bars=ax.barh(labels,values,color=["#1F6B62","#3C8178","#C17C38","#D3A568"])
    ax.set_xlim(0,100); ax.invert_yaxis(); ax.set_xlabel("演示评分（0—100）")
    ax.set_title("银龄金融健康指数：78分",fontsize=16,weight="bold",color="#183A43")
    ax.grid(axis="x",alpha=.2)
    for b,v in zip(bars,values): ax.text(v+1,b.get_y()+b.get_height()/2,str(v),va="center",fontsize=10)
    fig.tight_layout(); fig.savefig(path,dpi=200,bbox_inches="tight",facecolor="white"); plt.close(fig)


def setup_document():
    doc=Document()
    sec=doc.sections[0]
    sec.top_margin=Cm(2.2); sec.bottom_margin=Cm(2.1); sec.left_margin=Cm(2.4); sec.right_margin=Cm(2.2)
    styles=doc.styles
    normal=styles["Normal"]
    normal.font.name="宋体"; normal._element.rPr.rFonts.set(qn("w:eastAsia"),"宋体"); normal.font.size=Pt(11)
    for i,(size,color) in enumerate([(18,(24,58,67)),(15,(31,107,98)),(12.5,(166,106,52))],start=1):
        st=styles[f"Heading {i}"]
        st.font.name="微软雅黑"; st._element.rPr.rFonts.set(qn("w:eastAsia"),"微软雅黑"); st.font.size=Pt(size); st.font.bold=True; st.font.color.rgb=RGBColor(*color)
        st.paragraph_format.space_before=Pt(12 if i==1 else 8); st.paragraph_format.space_after=Pt(6)
    # Header/footer
    hp=sec.header.paragraphs[0]; hp.alignment=WD_ALIGN_PARAGRAPH.RIGHT
    rr=hp.add_run("忆路银龄｜养老金融赛道项目书"); set_font(rr,"微软雅黑",8,False,(90,110,115))
    fp=sec.footer.paragraphs[0]; fp.alignment=WD_ALIGN_PARAGRAPH.CENTER
    rr=fp.add_run("本项目不执行真实交易，不构成投资建议，不承诺收益    ·    "); set_font(rr,"宋体",8,False,(100,100,100))
    fld=OxmlElement("w:fldSimple"); fld.set(qn("w:instr"),"PAGE"); fp._p.append(fld)
    return doc


def add_cover(doc):
    p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before=Pt(75)
    r=p.add_run(TITLE); set_font(r,"微软雅黑",32,True,(24,58,67))
    p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before=Pt(12)
    r=p.add_run(SUBTITLE); set_font(r,"微软雅黑",18,True,(31,107,98))
    p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before=Pt(30)
    r=p.add_run("第十七届“工行杯”全国大学生金融科技创新大赛"); set_font(r,"宋体",13,True,(75,85,90))
    p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER
    r=p.add_run("参赛方向：养老金融"); set_font(r,"微软雅黑",12,True,(193,124,56))
    doc.add_paragraph("\n\n")
    box=doc.add_table(rows=1,cols=1); box.alignment=WD_TABLE_ALIGNMENT.CENTER
    shade(box.cell(0,0),"EEF5F3"); set_cell_text(box.cell(0,0),"AI理解需求｜规则守住边界｜知识库提供依据｜银行完成合规服务",True,(31,107,98),11)
    p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before=Pt(110)
    r=p.add_run("项目书 V1.0  ·  2026年9月"); set_font(r,"宋体",10,False,(100,100,100))
    doc.add_page_break()


def write_sections(doc):
    add_heading(doc,"项目声明",1)
    add_para(doc,"本项目为金融科技竞赛演示原型，聚焦养老金融知识服务、辅助规划、风险提示与适老化交互。系统不接入真实账户、不执行转账或投资交易；演示中的金融产品为模拟知识条目，资金比例、风险评分和健康指数为可解释的比赛演示规则，不构成个人投资、保险或购买建议，不承诺任何收益。正式落地必须在持牌金融机构的产品适当性、数据授权、合规审核和人工复核框架内运行。")
    add_heading(doc,"执行摘要",1)
    for t in [
        "随着养老金融和数字金融服务深入发展，银龄客户的需求已从存取款和人工咨询，延伸到养老资金安排、金融知识理解、风险识别、持续陪伴与家庭协同。传统数字渠道多以产品和功能为中心，难以连续理解用户的年龄、退休收入、资产规模、风险态度和养老目标；通用大模型虽然能够对话，但在金融场景中仍存在依据不透明、关键判断不可控和输出边界不清等问题。",
        "忆路银龄定位为面向银龄客户的养老金融智能决策辅助平台。平台以用户画像为起点，以多Agent编排为技术中枢，以RAG为可信知识底座，以规则化风险评分和养老资金用途规划为决策层，形成“需求识别—画像分析—风险评估—资金规划—知识检索—产品类别匹配—报告生成—诈骗防护—家庭协同”的服务闭环。",
        "技术上，项目采用“确定性规则 + 多Agent编排 + RAG知识增强 + LLM适老化生成”的分层架构。风险等级、资金用途和健康评分由独立规则模块计算，大模型主要负责知识整合和易懂表达；检索模块返回标题、内容与相似度，并在前端展示参考来源。DeepSeek、Qwen与Mock客户端均通过统一接口接入，外部服务异常时可降级。",
        "工程上，平台已实现Java 21与Spring Boot 3后端、Vue 3与TypeScript前端、JWT认证、用户画像、金融规划、六节点Agent、RAG/pgvector、诈骗文本与OCR检测、金融健康指数、家庭摘要、管理统计，以及dev/demo双环境。Demo模式无需数据库、Docker或外部API，可在比赛电脑离线启动。",
        "项目不以销售高收益产品为目标，而以降低银龄客户理解门槛、提升服务连续性、强化风险提醒和辅助客户经理沟通为价值。未来可作为手机银行适老版、养老金融专区、远程银行或网点客户经理工作台中的能力组件，通过银行授权数据、合规产品目录、审核知识库和人工复核机制实现进一步落地。"
    ]: add_para(doc,t)
    add_table(doc,["项目要素","核心内容"],[
        ["目标用户","银龄客户及其家庭；银行养老金融服务人员"],
        ["核心问题","金融信息难理解、养老需求难结构化、风险难判断、服务不连续"],
        ["核心方案","多Agent决策链、RAG来源、规则风控、适老化报告、诈骗防护"],
        ["已实现形态","可运行Web Demo；dev/demo双环境；Swagger与一键启动交付"],
        ["明确边界","无真实交易、无收益承诺、模拟产品、辅助决策而非替代专业人员"]
    ],[3,12])
    page_break(doc)

    add_heading(doc,"1 项目背景与问题定义",1)
    add_heading(doc,"1.1 赛道与场景",2)
    add_para(doc,"第十七届“工行杯”全国大学生金融科技创新大赛将养老金融列为创新方向，并强调从真实金融产品与服务场景中发现需求、体现新技术应用和银行业务模式创新。忆路银龄选择养老金融作为唯一主赛道，以数字金融能力和金融安全能力作为两条支撑线，不将项目泛化为通用聊天软件。")
    add_heading(doc,"1.2 银龄客户旅程中的四类断点",2)
    add_table(doc,["断点","典型表现","平台回应"],[
        ["理解断点","术语复杂、规则分散，难以判断信息是否适合自己","适老化解释、分步骤回答、RAG来源展示"],
        ["规划断点","收入、资产、医疗和旅行需求未形成统一视图","画像结构化、养老阶段识别、三类资金用途规划"],
        ["风险断点","高收益、验证码、点击链接等诱导信息难辨真伪","规则评分、AI辅助解释、文本与OCR检测"],
        ["陪伴断点","一次问答后缺少持续记录，家庭成员难以共同关注","规划留痕、健康指数、家庭协同摘要"]
    ],[2.4,6.2,6.2])
    add_heading(doc,"1.3 问题边界",2)
    add_bullets(doc,[
        "项目解决“理解、规划、风险提示与陪伴”问题，不处理资金清算、账户控制或真实产品申购。",
        "系统的金融产品匹配是模拟类别匹配，用于解释适配逻辑，不替代银行适当性评估。",
        "真实需求验证尚需补充银龄用户和金融从业者访谈；项目书不虚构问卷数量或合作机构。"
    ])

    add_heading(doc,"2 用户、场景与价值主张",1)
    add_heading(doc,"2.1 三类目标用户",2)
    add_table(doc,["用户","核心诉求","平台价值"],[
        ["银龄客户","听得懂、看得清、知道下一步该做什么","把复杂信息转为结构化、适老化建议"],
        ["家庭成员","了解长辈状态，及时发现异常和风险","家庭摘要与风险提醒，促进共同核对"],
        ["银行服务人员","快速掌握客户需求并保持合规边界","画像、规划、来源和风险摘要辅助沟通"]
    ],[3,5.8,6])
    add_heading(doc,"2.2 典型场景",2)
    add_bullets(doc,[
        "养老规划：68岁、每月退休收入6000元、50万元资产，希望兼顾医疗和旅行。",
        "知识咨询：询问个人养老金、银行养老服务或手机银行安全使用方法。",
        "风险防护：收到“养老金账户异常，请点击链接认证”的短信或截图。",
        "家庭协同：家人希望定期查看风险提醒，但不直接控制老人的金融决策。"
    ])
    add_heading(doc,"2.3 一句话价值主张",2)
    p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER
    r=p.add_run("不是给老年人增加一个聊天入口，而是把分散的养老金融服务连接成一条可解释、可追踪、可降级的决策辅助链。"); set_font(r,"微软雅黑",14,True,(31,107,98))
    page_break(doc)

    add_heading(doc,"3 总体解决方案",1)
    doc.add_picture(str(FIG/"business-flow.png"),width=Cm(16.2)); caption(doc,"图3-1 养老金融智能决策与陪伴闭环")
    add_heading(doc,"3.1 服务闭环",2)
    add_para(doc,"用户以自然语言或表单描述年龄、收入、资产、风险态度和养老目标。系统先识别意图并补齐画像，再用独立规则计算风险与资金用途；RAG检索受控知识片段，产品匹配服务依据年龄与风险偏好返回模拟类别；最后由LLM或本地模板生成适老化报告。诈骗检测、健康指数和家庭摘要作为跨阶段能力，分别覆盖守钱、持续观察和共同陪伴。")
    add_heading(doc,"3.2 核心功能矩阵",2)
    add_table(doc,["功能域","已实现能力","展示重点"],[
        ["身份与画像","注册登录、JWT、个人信息、银龄画像","同一问题因画像不同而调整表达"],
        ["AI养老助手","对话、Prompt、RAG、来源、可信度","回答依据可见，模型可替换"],
        ["养老金融规划","阶段判断、风险等级、三类用途、模拟产品、六段报告","金融链不是一次聊天"],
        ["金融智能体","意图、画像、风险、规划、匹配、报告六节点","执行状态和中间结果可观测"],
        ["风险防护","文本规则、AI辅助分析、OCR图片检测、记录","规则评分与解释分离"],
        ["健康与家庭","四维健康分、风险提醒、家庭摘要","持续陪伴而非一次问答"],
        ["管理与工程","统计、趋势、Swagger、Docker、Demo一键启动","现场稳定与后续扩展"]
    ],[3,7.3,5])

    add_heading(doc,"4 AI金融智能体设计",1)
    doc.add_picture(str(FIG/"agent-flow.png"),width=Cm(16.2)); caption(doc,"图4-1 六节点金融智能体编排")
    add_heading(doc,"4.1 为什么不是一个Prompt",2)
    add_para(doc,"养老金融任务同时包含意图分类、信息补齐、风险边界、数值规划、知识检索和自然语言表达。若全部交给一次大模型调用，错误难定位、结果难测试、规则难审计。项目通过FinancialAgentOrchestrator把任务拆为专业节点，每次请求使用独立AgentContext保存中间结果，产品匹配和LLM各调用至多一次。")
    add_table(doc,["节点","输入","结构化输出","可验证性"],[
        ["意图识别Agent","用户自然语言","意图类型、置信度","关键词与分类单测"],
        ["用户画像Agent","消息、画像、历史规划","年龄阶段、能力、标签、假设","字段来源可追踪"],
        ["风险评估Agent","年龄、资产、收入、偏好","0—100分、等级、警告","阈值与边界单测"],
        ["资金规划Agent","结构化请求","三类资金用途与比例","确定性规则测试"],
        ["产品匹配Agent","年龄、风险、目标","模拟产品类别及理由","数据库/内存双实现"],
        ["报告生成Agent","画像、规划、知识、产品","固定六段式报告","LLM异常模板降级"]
    ],[2.7,3.2,5.2,4])
    add_heading(doc,"4.2 风险评分模型",2)
    add_para(doc,"RiskScoreCalculator综合年龄、资产、月收入和风险偏好形成0—100演示分。0—30为低风险、31—60为稳健风险、61—100为较高风险。该分数用于风险教育和流程展示，不是金融机构客户风险承受能力测评，也不用于真实产品准入。")
    page_break(doc)

    add_heading(doc,"5 RAG可信知识与大模型边界",1)
    add_heading(doc,"5.1 知识处理链",2)
    add_para(doc,"知识库支持PDF、TXT与Markdown文档，经MinIO保存、文档解析、文本切片、Embedding向量化后写入PostgreSQL/pgvector。查询时，问题向量与知识块执行相似度检索，返回TopK标题、分类、内容和相似度，随后注入Prompt并随答案展示来源。复杂向量基础设施不可用时，VectorStore可降级到Mock实现。")
    add_table(doc,["阶段","实现","风险控制"],[
        ["采集","管理员上传PDF/TXT/Markdown","文件类型与大小校验；来源需审核"],
        ["解析切片","Parser + TextSplitter","保留标题和文档关联"],
        ["向量化","OpenAI/Qwen/Mock Embedding抽象","维度检查；无Key降级"],
        ["检索","PgVectorStore / MockVectorStore","TopK、相似度与来源字段"],
        ["生成","DeepSeek/Qwen/Mock LlmClient","超时与异常降级；不打印密钥"],
        ["呈现","答案、可信度、来源与风险提示","用户可判断依据，不声称消除幻觉"]
    ],[2.5,6.4,6.3])
    add_heading(doc,"5.2 Prompt工程",2)
    add_para(doc,"养老助手Prompt固定角色为“面向银龄用户的金融陪伴助手”，要求优先引用知识库、使用老人容易理解的语言、避免专业术语、分步骤解释、主动提示风险、不提供买卖建议、不承诺收益，并采用“简单解释—具体建议—风险提醒—参考依据”的格式。金融规划报告采用“用户情况—养老需求分析—资金规划建议—金融产品建议—风险提醒—温馨提示”六段式结构。")
    add_heading(doc,"5.3 可信度机制",2)
    add_para(doc,"ConfidenceService根据检索数量、是否命中知识、相似度和回答长度计算0—1展示分。分数用于提示“回答依据充足程度”，不是事实真伪概率。当前30题质量评估问题集已完成，真实DeepSeek和pgvector批量评估指标仍待在可控环境中联调，项目不编造知识命中率或平均可信度。")

    add_heading(doc,"6 养老金融规划与产品类别匹配",1)
    add_heading(doc,"6.1 规划输入与阶段识别",2)
    add_para(doc,"规划接口接收年龄、退休月收入、资产规模、风险偏好、养老目标，以及可选的医疗需求和旅行需求。60岁以下定义为退休准备阶段，60—69岁定义为退休初期，70岁及以上定义为养老保障阶段。缺少可选需求时使用明确默认描述，不阻断流程。")
    add_heading(doc,"6.2 三类资金用途",2)
    add_table(doc,["用途","演示比例","含义"],[
        ["生活备用资金","20%","日常生活与突发支出，强调可用性"],
        ["养老保障资金","60%","长期养老和医疗保障，强调安全与稳定"],
        ["灵活消费资金","20%","旅行、兴趣和家庭陪伴等弹性需求"]
    ],[4,3,8])
    add_para(doc,"以50万元资产为例，系统演示分配10万元生活备用、30万元养老保障和10万元灵活消费。该比例为竞赛原型中的可解释默认规则，实际银行服务必须结合客户负债、现金流、医疗保障、家庭责任、产品适当性和监管要求重新评估。")
    add_heading(doc,"6.3 模拟产品知识库",2)
    add_para(doc,"financial_product表初始化10条模拟条目，包括个人养老金、养老储蓄、养老保险、长期护理保障、家庭应急储备和数字金融安全服务等。每条包含产品类型、风险等级、适龄范围、流动性描述、知识性介绍和适合标签；所有收益字段均明确“不展示或承诺收益，以正式规则为准”。")
    add_heading(doc,"6.4 产品匹配原则",2)
    add_bullets(doc,[
        "低风险与稳健银龄客户优先显示养老储蓄、个人养老金、保障和应急类知识条目。",
        "只有在年龄、偏好和目标满足演示规则时，才增加稳健型养老理财知识条目。",
        "高风险偏好不会导致系统主动鼓励高风险产品，反而强化风险提示。",
        "每项推荐均返回推荐原因和风险提示，且明确为模拟类别信息。"
    ])
    page_break(doc)

    add_heading(doc,"7 金融安全、健康指数与家庭协同",1)
    add_heading(doc,"7.1 诈骗风险检测",2)
    add_para(doc,"诈骗模块复用同一FraudDetectionService链路。文本或OCR识别内容先进入RuleEngine，验证码、转账、点击链接、高收益、养老补贴、中奖等关键词按权重计分；FraudAiAnalyzer用于提供诈骗类型和解释。最终融合为LOW、MEDIUM、HIGH或CRITICAL等级并保存记录。规则决定底线分值，AI不直接替代风险规则。")
    add_heading(doc,"7.2 OCR与对象存储",2)
    add_para(doc,"图片检测流程为“上传—文件校验—MinIO保存—OCR识别—文本风险检测—记录保存”。OcrClient抽象支持Mock与真实厂商适配，缺少密钥时自动降级。Demo模式不依赖MinIO或OCR外部服务，使用本地模拟结果保证现场流程完整。")
    add_heading(doc,"7.3 银龄金融健康指数",2)
    doc.add_picture(str(FIG/"health-score.png"),width=Cm(13.5)); caption(doc,"图7-1 金融健康指数演示样例")
    add_para(doc,"指数由资产安全、养老准备、风险控制和医疗保障四个维度等权汇总，Demo样例总分78分。每项均限制在0—100，并返回风险提醒与免责声明。该指数不是产品评级、征信评分或投资能力测评，而是帮助用户理解“哪些方面需要进一步准备”的沟通工具。")
    add_heading(doc,"7.4 家庭协同",2)
    add_para(doc,"家庭摘要展示老人姓名、年龄、养老状态和风险提醒，例如每月共同核对养老资金安排、收到转账或补贴链接时先联系家人核实。设计遵循“陪伴而非代替”：家庭成员获得必要摘要，但不默认获得账户操作权；正式产品需进一步建立老人授权、权限分级与撤回机制。")

    add_heading(doc,"8 技术架构与数据设计",1)
    doc.add_picture(str(FIG/"architecture.png"),width=Cm(16.2)); caption(doc,"图8-1 分层技术架构")
    add_heading(doc,"8.1 技术栈",2)
    add_table(doc,["层次","技术","职责"],[
        ["前端","Vue 3、TypeScript、Vite、Element Plus、ECharts","适老化交互、Agent动画、规划与统计图表"],
        ["后端","Java 21、Spring Boot 3、Spring MVC、Spring Security","REST接口、认证、业务编排、异常治理"],
        ["持久化","MyBatis Plus、MySQL 8、Redis","用户、画像、消息、规划、风险记录与缓存"],
        ["AI与知识","DeepSeek/Qwen、Embedding、pgvector、Prompt","生成、向量检索、知识增强与来源"],
        ["文件与部署","MinIO、Docker Compose、Demo Jar","文档/图片存储、开发编排、离线演示"]
    ],[2.4,6.4,6.4])
    add_heading(doc,"8.2 核心数据实体",2)
    add_table(doc,["实体","关键字段","用途"],[
        ["sys_user / role","账号、密码摘要、状态、角色","认证与权限"],
        ["elderly_profile","年龄、收入、风险、数字能力、需求、标签","个性化画像"],
        ["ai_conversation / message","用户、会话、角色、内容、时间","对话留痕"],
        ["knowledge_chunk_vector","chunk、内容、embedding","RAG向量检索"],
        ["fraud_detection_record","输入、OCR、风险分、标签、解释","诈骗检测记录"],
        ["financial_product","类型、风险、适龄、流动性、标签","模拟产品知识"],
        ["elderly_financial_plan","输入快照、规划结果、时间","养老规划留痕"]
    ],[3.8,6,5.4])
    add_heading(doc,"8.3 API与安全",2)
    add_para(doc,"接口统一使用/api/v1前缀和Result<T>响应结构。Spring Security与JWT保护用户、画像、规划、Agent、健康评分和管理接口；ADMIN权限用于知识上传与管理统计。Swagger提供接口演示。密码使用BCrypt摘要，API Key通过环境变量注入且禁止写入日志或仓库。")
    page_break(doc)

    add_heading(doc,"9 工程实现与稳定性",1)
    add_heading(doc,"9.1 dev/demo双环境",2)
    add_table(doc,["能力","dev模式","demo模式"],[
        ["数据库","MySQL + Redis + PostgreSQL/pgvector","不连接外部数据库"],
        ["对象存储","MinIO","不连接MinIO"],
        ["大模型","DeepSeek/Qwen，失败可Mock降级","不调用外部API，固定模板"],
        ["产品与规划","数据库读写","内存模拟数据"],
        ["登录","数据库账号与JWT","admin / 123456，Demo JWT流程"],
        ["健康检查","探测真实依赖","全部返回MOCK"]
    ],[3,6.2,6])
    add_para(doc,"比赛现场可通过java -jar yinling-backend.jar --spring.profiles.active=demo启动。Demo交付目录包含Jar、start-demo.bat、账号说明与README；启动脚本检查Java版本、端口占用和Jar启动结果，并输出Swagger与健康检查地址。")
    add_heading(doc,"9.2 异常与降级",2)
    add_bullets(doc,[
        "LLM调用记录provider、model、耗时、成功与fallback，不记录完整API Key。",
        "LLM失败返回本地模板；RAG不可用返回空知识或Mock检索；核心规则结果仍可生成。",
        "OCR缺少厂商密钥自动使用Mock；对象存储禁用时返回稳定演示URL。",
        "统一异常处理覆盖参数校验、业务异常、认证异常和系统异常，并返回traceId。"
    ])
    add_heading(doc,"9.3 验证策略",2)
    add_para(doc,"后端测试覆盖认证、画像、对话、诈骗检测、OCR、对象存储、统计趋势、金融规划、Agent编排、健康评分和权限；外部DeepSeek与pgvector集成测试通过环境变量条件启用。前端使用npm run build验证类型与生产构建。最终提交前应保留测试日志、构建产物哈希和Demo录屏，作为可运行性证据。")
    add_table(doc,["测试层","重点","完成标准"],[
        ["单元测试","规则阈值、意图、资金比例、置信度、健康分","边界值可重复"],
        ["接口测试","JWT、403/401、响应结构、记录保存","核心接口闭环"],
        ["集成测试","真实LLM、pgvector、文档上传","有配置执行，无配置明确跳过"],
        ["前端构建","TypeScript与Vite生产构建","无编译错误"],
        ["现场演练","无Docker、无网络启动与核心流程","一键启动，录屏备份"]
    ],[2.4,7.2,5.6])

    add_heading(doc,"10 核心创新",1)
    add_table(doc,["创新点","常见做法","忆路银龄方案","可验证证据"],[
        ["任务化多Agent","一次Prompt直接生成","拆为意图、画像、风险、规划、匹配、报告节点","AgentChain状态、耗时与中间结果"],
        ["规则与生成解耦","让LLM直接判断金融风险","规则计算关键分值，LLM负责解释","RiskScoreCalculator与单测"],
        ["可信RAG","只依赖模型记忆","检索受控知识并展示标题、内容、相似度","VectorStore、来源卡片"],
        ["银龄服务闭环","单一问答或单点防骗","规划、知识、安全、健康、家庭协同联动","多页面可运行Demo"],
        ["双环境工程化","现场依赖云端服务","dev真实能力 + demo全本地降级","application-demo与一键脚本"]
    ],[2.4,3.5,5.7,4.2])
    add_para(doc,"项目壁垒不在某个大模型品牌，而在养老金融任务拆解、业务规则、知识治理、风险边界、适老化交互和银行服务流程的组合。模型、OCR和向量库均可替换，场景方法论与可验证的业务链是平台核心。")
    page_break(doc)

    add_heading(doc,"11 数据安全、合规与伦理",1)
    add_heading(doc,"11.1 四类风险分层治理",2)
    add_table(doc,["风险","当前控制","正式落地需补充"],[
        ["金融业务风险","不交易、不承诺收益、模拟产品、免责声明","持牌机构适当性、产品目录与人工复核"],
        ["模型风险","规则与LLM解耦、RAG来源、模板降级","模型评测、红队测试、提示词与知识版本审计"],
        ["数据风险","JWT/RBAC、BCrypt、密钥环境变量、最小字段","授权撤回、数据删除、加密、访问审计、分级分类"],
        ["运行风险","健康检查、外部服务降级、Demo离线模式","高可用、限流、监控告警、灾备与应急预案"]
    ],[2.5,6.6,6.4])
    add_heading(doc,"11.2 人机边界",2)
    add_para(doc,"系统输出必须被理解为辅助信息。对高风险诈骗、异常账户、真实产品选择、保险条款、税务规则和资金操作，应主动引导用户联系银行官方渠道或专业人员。正式落地可为高风险节点设置强制人工复核，并保留输入、规则版本、知识来源和输出日志，支持追溯。")
    add_heading(doc,"11.3 适老化伦理",2)
    add_bullets(doc,[
        "不以年龄推定能力，不将“老年”标签等同于低认知或低风险承受能力。",
        "用简明语言解释专业概念，但不隐去关键风险、费用、期限和限制条件。",
        "家庭协同基于本人授权，避免以关怀名义剥夺用户自主决策权。",
        "高对比、大字号、清晰按钮和有限步骤降低操作负担，同时保留返回与纠错路径。"
    ])

    add_heading(doc,"12 银行落地与商业模式",1)
    add_heading(doc,"12.1 B2B2C能力组件",2)
    add_para(doc,"忆路银龄不以独立销售金融产品为目标，而以能力组件嵌入银行养老金融服务体系。客户侧获得易懂的规划和风险提醒；客户经理侧获得结构化画像、沟通摘要与知识依据；管理侧可在匿名化和聚合化前提下观察需求类型、风险事件和服务效果。")
    add_heading(doc,"12.2 可嵌入触点",2)
    add_table(doc,["触点","使用方式","价值"],[
        ["手机银行适老版","AI助手、规划中心、诈骗检测、健康指数","提升自助服务完成率与安全感"],
        ["养老金融专区","基于授权画像解释制度与产品类别","形成连续养老服务入口"],
        ["远程银行","会前收集需求、会中展示依据、会后生成摘要","减少重复沟通"],
        ["网点客户经理工作台","查看画像、规划、风险与知识来源","辅助标准化服务，不替代合规判断"]
    ],[3.2,6.4,6])
    add_heading(doc,"12.3 价值指标",2)
    add_bullets(doc,[
        "咨询完成率、平均规划生成时间、知识来源展示率；",
        "高风险提醒触达率、人工转接率、诈骗线索复核率；",
        "银龄客户满意度、适老化任务完成率、家庭协同授权率；",
        "客户经理沟通准备时间、重复问题减少量和合规复核覆盖率。"
    ])
    add_heading(doc,"12.4 推广路线",2)
    add_table(doc,["阶段","范围","主要工作","退出条件"],[
        ["竞赛PoC","本地Demo","证明链路、交互、降级与安全边界","核心流程稳定可演示"],
        ["受控试点","单一触点、小规模志愿用户","访谈、可用性测试、知识审核","关键指标达标且无重大风险"],
        ["银行内测","沙箱环境、客户经理辅助","接入授权数据与合规目录、人工复核","安全与合规验收"],
        ["分阶段推广","适老版/养老专区/远程银行","监控、审计、模型治理与持续优化","运营指标持续改善"]
    ],[2.2,3.3,6.3,3.8])
    page_break(doc)

    add_heading(doc,"13 竞争分析",1)
    add_table(doc,["方案","优势","局限","本项目差异"],[
        ["传统智能客服","稳定、规则明确、业务入口成熟","以问答和导航为主，连续画像与规划有限","连接画像、规则规划、风险与家庭协同"],
        ["通用大模型助手","语言自然、知识覆盖广","金融依据、合规边界和数据治理不足","受控RAG、规则计算、来源与降级"],
        ["单点防诈工具","风险识别聚焦","无法覆盖养老规划和持续陪伴","防诈作为养老金融闭环的一环"],
        ["传统财富规划工具","结构化、规则明确","交互门槛较高、适老化和知识解释有限","自然语言入口、多Agent与适老化报告"]
    ],[3,4.1,4.1,4.8])
    add_para(doc,"忆路银龄不宣称在模型参数或单一算法上形成绝对领先，其竞争力来自场景整合与工程落地：养老金融主线、金融安全支撑、规则边界、知识来源、适老化体验和无外部依赖Demo共同构成完整方案。")

    add_heading(doc,"14 实施进度、团队与资源",1)
    add_heading(doc,"14.1 已完成里程碑",2)
    add_table(doc,["阶段","成果"],[
        ["Sprint 1—3","工程基础、JWT认证、用户信息与银龄画像、Prompt个性化"],
        ["Sprint 4—6","文本诈骗、OCR、对象存储、管理统计与风险趋势"],
        ["Sprint 7—9.1","LLM/Embedding抽象、pgvector、知识上传、可信度与健康探测"],
        ["Sprint 10","养老金融规划、模拟产品匹配、六段式AI报告"],
        ["Sprint 11","六节点金融智能体、RAG来源、风险评分与执行链"],
        ["Sprint 12","Agent动画、金融健康指数、家庭协同与比赛Demo增强"]
    ],[3.5,11.7])
    add_heading(doc,"14.2 赛前工作重点",2)
    add_bullets(doc,[
        "补充5—10位银龄用户和1—3位金融服务人员的真实访谈，保留匿名记录；",
        "整理知识库权威来源、发布日期、版本和审核人，完成知识治理清单；",
        "在可控环境完成30题DeepSeek + pgvector真实评估，不足项明确标记；",
        "完成项目书、架构图、测试报告、Demo录屏、五分钟答辩和无网络演练；",
        "冻结比赛版本并生成Git标签、构建哈希和交付备份。"
    ])
    add_heading(doc,"14.3 团队分工建议",2)
    add_para(doc,"在不虚构成员信息的前提下，建议按三类职责组织：项目与金融负责人负责需求、业务规则、合规和答辩；AI与后端负责人负责Agent、RAG、LLM、数据与部署；前端与产品负责人负责适老化交互、可视化、用户测试和演示材料。最终报名版应替换为真实成员姓名、专业与贡献。")
    add_heading(doc,"14.4 PoC资源预算",2)
    add_table(doc,["项目","估算范围","说明"],[
        ["模型与Embedding测试","100—800元","按真实联调调用量控制，Demo不产生调用"],
        ["云主机/对象存储（可选）","100—500元","比赛前短期沙箱，非现场必需"],
        ["调研与打印","100—400元","访谈交通、材料与展示备份"],
        ["合计","约300—1700元","内部PoC估算，不含差旅，不是官方预算"]
    ],[5,4,6.2])
    page_break(doc)

    add_heading(doc,"15 风险与应对",1)
    add_table(doc,["风险","可能影响","应对措施"],[
        ["真实需求证据不足","被认为技术先行、场景后补","开展小规模访谈和可用性测试，不补造数据"],
        ["大模型幻觉","错误解释金融规则","RAG来源、规则校验、风险提示、人工复核"],
        ["知识过期","回答依据失效","来源登记、版本控制、定期审核与下架机制"],
        ["外部服务故障","现场演示中断","Demo全本地、模板降级、录屏与静态截图备份"],
        ["规划规则过简","被误解为真实建议","明确演示边界，增加规则说明与银行适当性接口"],
        ["敏感数据泄露","隐私与合规风险","最小化、授权、加密、RBAC、审计与删除机制"],
        ["家庭协同越权","损害老人自主权","本人授权、最小摘要、权限分级与可撤回"]
    ],[3.3,4.5,7.4])

    add_heading(doc,"16 比赛Demo演示方案",1)
    add_heading(doc,"16.1 五分钟核心流程",2)
    add_table(doc,["时间","操作","评委看到的结论"],[
        ["0:00—0:30","一句话痛点与业务闭环图","项目解决养老金融服务连续性，不是普通聊天"],
        ["0:30—1:40","输入“68岁、50万元、6000元退休金”运行Agent","六节点依次执行，规则与AI分工可见"],
        ["1:40—2:30","展示规划、模拟产品理由和RAG来源","金融决策链有依据、有边界"],
        ["2:30—3:20","输入养老金异常点击链接或上传截图","养老规划之外还能守护资金安全"],
        ["3:20—4:10","展示健康指数与家庭摘要","从一次问答扩展到持续陪伴"],
        ["4:10—5:00","架构、Demo降级、银行落地与免责声明","做得出来、能稳定演示、知道如何合规落地"]
    ],[2.2,6.2,6.8])
    add_heading(doc,"16.2 演示保障",2)
    add_bullets(doc,[
        "主方案：本机Demo profile；备用方案：预构建Jar + start-demo.bat。",
        "二级备份：90秒核心录屏、关键页面高清截图和接口响应JSON。",
        "演示前检查Java版本、8080/5173端口、浏览器缓存、账号和投屏比例。",
        "不在答辩现场临时切换真实API Key，不展示任何密钥或真实个人数据。"
    ])

    add_heading(doc,"17 社会价值与预期成果",1)
    add_para(doc,"忆路银龄的社会价值在于提升银龄群体对养老金融信息的理解能力、风险意识和自主决策支持，而不是追求更多交易。通过把复杂概念转为简明步骤、把风险依据显式展示、把诈骗防护嵌入规划旅程、把家庭陪伴建立在授权基础上，平台有望降低数字金融使用门槛，促进养老金融教育与适老服务。")
    add_para(doc,"项目当前预期成果包括可运行Web原型、完整前后端代码、数据库脚本、API文档、Demo一键启动包、30题评估问题集及比赛项目书。未来成果目标为经过真实用户验证的适老交互规范、经过审核的养老金融知识清单、可复现的模型质量报告和银行沙箱试点方案。所有未来目标均需以实际验证为准。")

    add_heading(doc,"18 结论",1)
    add_para(doc,"忆路银龄围绕养老金融真实服务链，将用户画像、风险评估、资金用途规划、知识检索、产品类别匹配、报告生成、诈骗防护、健康观察和家庭协同连接为完整闭环。系统不把关键金融判断完全交给大模型，而以业务规则守住底线、RAG提供依据、Agent完成编排、LLM完成适老化表达；同时通过dev/demo隔离和多级降级保证工程可运行性。")
    add_para(doc,"项目的下一步不是继续堆叠功能，而是补强真实需求证据、知识治理、模型评估和银行合规落地。其核心价值在于证明：面向银龄客户的AI金融服务可以既易懂，也可解释；既体现技术创新，也尊重金融边界。")
    page_break(doc)

    add_heading(doc,"参考资料",1)
    refs=[
        "[1] 第十七届“工行杯”全国大学生金融科技创新大赛公开赛事资料与赛事指引，检索日期：2026-09-20。",
        "[2] 中国工商银行“工行杯”往期优秀作品公开页面：AI绿鉴、e企融、青葵宝、智盾链等，检索日期：2026-09-20。",
        "[3] 忆路银龄项目仓库README、Sprint 1—12 API文档、数据库脚本与测试代码，版本：Git commit 8b7294b。",
        "[4] 《个人养老金实施办法》及个人养老金制度相关公开政策资料。正式知识库接入时应记录具体发布机关、文号、日期与版本。",
        "[5] 《中华人民共和国个人信息保护法》《中华人民共和国数据安全法》《中华人民共和国网络安全法》。",
        "[6] DeepSeek与通义千问OpenAI兼容接口公开技术文档；PostgreSQL pgvector技术文档。",
        "[7] 项目研究材料：《“忆路银龄”工行杯项目书、PPT与五分钟答辩全套规划》，2026-09-20。"
    ]
    for x in refs: add_para(doc,x)
    add_para(doc,"说明：研究材料未保留部分赛事网页的完整URL。正式提交前，应由团队依据报名页面和官方通知补齐可访问链接、发布日期与访问日期；如学校或赛区下发统一项目书模板，以该模板为准。")

    add_heading(doc,"附录A 主要接口",1)
    add_table(doc,["接口","方法","用途","权限"],[
        ["/api/v1/auth/login","POST","登录并返回JWT","公开"],
        ["/api/v1/profile/me","GET/PUT","查询与更新银龄画像","JWT"],
        ["/api/v1/assistant/chat","POST","AI养老金融问答","JWT"],
        ["/api/v1/financial/plan","POST","生成养老金融规划","JWT"],
        ["/api/v1/financial/agent/chat","POST","运行六节点金融智能体","JWT"],
        ["/api/v1/financial/health-score","GET","金融健康指数","JWT"],
        ["/api/v1/financial/family-summary","GET","家庭协同摘要","JWT"],
        ["/api/v1/fraud/detect/text","POST","文本诈骗检测","JWT"],
        ["/api/v1/fraud/detect/image","POST","OCR图片诈骗检测","JWT"],
        ["/api/v1/admin/knowledge/upload","POST","知识文档处理入库","ADMIN"],
        ["/api/v1/system/health","GET","系统依赖健康状态","公开"]
    ],[6.2,2.1,5.3,2])
    add_heading(doc,"附录B Demo启动",1)
    add_para(doc,"比赛电脑需安装Java 21。进入交付目录，双击start-demo.bat，或执行：")
    p=doc.add_paragraph(); shade_dummy=None
    r=p.add_run("java -jar yinling-backend.jar --spring.profiles.active=demo"); set_font(r,"Consolas",10,False,(31,107,98))
    add_bullets(doc,[
        "账号：admin；密码：123456。",
        "Swagger：http://localhost:8080/swagger-ui/index.html",
        "健康检查：http://localhost:8080/api/v1/system/health",
        "Demo模式不连接MySQL、Redis、PostgreSQL、MinIO或DeepSeek。"
    ])
    add_heading(doc,"附录C 提交前证据清单",1)
    add_bullets(doc,[
        "补齐真实团队、学校、指导教师与联系方式，仅使用报名系统一致信息。",
        "保留mvn test与npm run build完整日志，并记录测试通过/跳过数量。",
        "在配置真实服务的环境中运行30题评估，禁止以Mock结果冒充真实指标。",
        "整理每条知识的来源、发布日期、版本、审核状态和下架机制。",
        "完成银龄用户访谈、银行服务人员访谈和可用性测试，匿名归档原始证据。",
        "导出PDF后检查目录、页码、图表、字体、链接与文件大小，再于官方截止前上传。"
    ])


def build_markdown(doc):
    """Export the same document body as a version-control-friendly Markdown file."""
    from docx.table import Table
    from docx.text.paragraph import Paragraph

    def blocks(parent):
        body = parent.element.body
        for child in body.iterchildren():
            if child.tag == qn("w:p"):
                yield Paragraph(child, parent)
            elif child.tag == qn("w:tbl"):
                yield Table(child, parent)

    lines = [f"# {TITLE}", "", f"## {SUBTITLE}", "", "> 参赛方向：养老金融", "> 本项目不执行真实交易，不构成投资建议，不承诺收益。", ""]
    for block in blocks(doc):
        if isinstance(block, Paragraph):
            text_value = block.text.strip()
            if not text_value or text_value in {TITLE, SUBTITLE}:
                continue
            style = block.style.name if block.style else ""
            if style.startswith("Heading"):
                try:
                    level = int(style.split()[-1]) + 1
                except ValueError:
                    level = 2
                lines.extend(["#" * min(level, 6) + " " + text_value, ""])
            elif style.startswith("List Bullet"):
                lines.append(f"- {text_value}")
            elif text_value.startswith("图") and "-" in text_value[:8]:
                lines.extend([f"*{text_value}*", ""])
            elif "http://" in text_value or text_value.startswith("java -jar"):
                lines.extend(["```text", text_value, "```", ""])
            else:
                lines.extend([text_value, ""])
        else:
            rows = [[c.text.replace("\n", " ").strip() for c in row.cells] for row in block.rows]
            if not rows:
                continue
            lines.append("| " + " | ".join(rows[0]) + " |")
            lines.append("| " + " | ".join(["---"] * len(rows[0])) + " |")
            for row in rows[1:]:
                lines.append("| " + " | ".join(row) + " |")
            lines.append("")
    MD.write_text("\n".join(lines), encoding="utf-8")


def main():
    draw_flow(FIG/"business-flow.png")
    draw_arch(FIG/"architecture.png")
    draw_agent(FIG/"agent-flow.png")
    draw_score(FIG/"health-score.png")
    doc=setup_document(); add_cover(doc); write_sections(doc)
    props=doc.core_properties
    props.title=f"{TITLE}项目书"; props.subject=SUBTITLE; props.author="忆路银龄项目团队"; props.keywords="养老金融, 多智能体, RAG, 银龄服务, 金融安全"
    doc.save(DOCX)
    build_markdown(doc)
    print(DOCX)
    print(MD)


if __name__ == "__main__":
    main()
