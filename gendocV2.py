import os
import re
import csv
from datetime import datetime

# Format time to be "2024-06-01 12:00:00"
current_time = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")

source_dir = "src"
output_csv = f"docs_{current_time}.csv"

DECL_PATTERN = re.compile(
    r"\b(?:(public|protected|private)\s+)?(abstract\s+)?(class|interface|enum)\s+(\w+)([^\{]*)\{"
)

KPI_INHERITANCE = "ใช้หลักการ การสืบทอด (Inheritance) กับ class & abstract class & interface  ที่ผู้ใช้สร้างขึ้นเอง"
KPI_ENCAPSULATION = "ใช้หลักการ การห่อหุ้ม (Encapsulation) กับ class & abstract class ที่ผู้ใช้สร้างขึ้นเอง"
KPI_OVERRIDE_OVERLOAD = "ใช้หลักการ Override / Overload  กับ method ที่ผู้ใช้สร้างขึ้นเอง (2 คะแนน)"
KPI_CUSTOM_CLASS = "ให้พัฒนา Class ขึ้นเอง (Custom Class)"
KPI_CUSTOM_ABSTRACT_CLASS = "ให้พัฒนา Abstract Class ขึ้นเอง (Custom Abstract Class)"
KPI_CUSTOM_INTERFACE = "ให้พัฒนา Interface ขึ้นเอง (Custom Interface)"
KPI_PARAM_CUSTOM_CLASS = "ให้พัฒนาเมธอดที่รับค่าเป็น Class ที่สร้างขึ้นเอง (Custom Class)"
KPI_PARAM_CUSTOM_ABSTRACT_CLASS = "ให้พัฒนาเมธอดที่รับค่าเป็น Abstract Class ที่สร้างขึ้นเอง (Custom Abstract Class)"
KPI_PARAM_CUSTOM_INTERFACE = "ให้พัฒนาเมธอดที่รับค่าเป็น Interface ที่สร้างขึ้นเอง (Custom Interface)"


def strip_generics(text):
    out = []
    depth = 0
    for ch in text:
        if ch == "<":
            depth += 1
            continue
        if ch == ">":
            depth = max(0, depth - 1)
            continue
        if depth == 0:
            out.append(ch)
    return "".join(out)


def split_types_list(text):
    cleaned = strip_generics(text)
    return [part.strip().split(".")[-1] for part in cleaned.split(",") if part.strip()]


def split_params(params_text):
    if not params_text.strip():
        return []

    parts = []
    buffer = []
    angle_depth = 0
    paren_depth = 0

    for ch in params_text:
        if ch == "<":
            angle_depth += 1
        elif ch == ">":
            angle_depth = max(0, angle_depth - 1)
        elif ch == "(":
            paren_depth += 1
        elif ch == ")":
            paren_depth = max(0, paren_depth - 1)

        if ch == "," and angle_depth == 0 and paren_depth == 0:
            parts.append("".join(buffer).strip())
            buffer = []
            continue

        buffer.append(ch)

    last = "".join(buffer).strip()
    if last:
        parts.append(last)
    return parts


def clean_param_type(param_text):
    # Remove leading annotations.
    text = param_text.strip()
    while text.startswith("@"):
        annotation_match = re.match(r"^@\w+(?:\([^)]*\))?\s*", text)
        if not annotation_match:
            break
        text = text[annotation_match.end():].lstrip()

    text = re.sub(r"\b(final|volatile|transient)\b\s*", "", text).strip()

    # Remove variable name: all tokens except the last token represent type.
    tokens = text.split()
    if len(tokens) >= 2:
        type_text = " ".join(tokens[:-1])
    else:
        type_text = tokens[0] if tokens else ""

    type_text = type_text.replace("...", "").replace("[]", "").strip()
    type_text = strip_generics(type_text).strip()
    type_text = type_text.split(".")[-1]
    return type_text


def infer_type_kind(kind, abstract_kw):
    if kind == "interface":
        return "Interface"
    if kind == "class" and abstract_kw:
        return "Abstract Class"
    if kind == "class":
        return "Class"
    return kind.capitalize()


def build_custom_type_index(java_files):
    custom_classes = set()
    custom_abstract_classes = set()
    custom_interfaces = set()

    for file_path in java_files:
        with open(file_path, "r", encoding="utf-8") as f:
            content = f.read()

        for match in DECL_PATTERN.finditer(content):
            abstract_kw = match.group(2)
            kind = match.group(3)
            type_name = match.group(4)

            if kind == "interface":
                custom_interfaces.add(type_name)
            elif kind == "class" and abstract_kw:
                custom_abstract_classes.add(type_name)
            elif kind == "class":
                custom_classes.add(type_name)

    return custom_classes, custom_abstract_classes, custom_interfaces


def gather_java_files(base_dir):
    files = []
    for root, _, names in os.walk(base_dir):
        for name in names:
            if name.endswith(".java"):
                files.append(os.path.join(root, name))
    return files


def extract_type_header(content):
    match = DECL_PATTERN.search(content)
    if not match:
        return None

    abstract_kw = match.group(2)
    kind = match.group(3)
    name = match.group(4)
    tail = match.group(5) or ""
    type_kind = infer_type_kind(kind, abstract_kw)

    extends_list = []
    implements_list = []

    extends_match = re.search(r"\bextends\s+([^\{\n]+)", tail)
    if extends_match:
        extends_list = split_types_list(extends_match.group(1))

    impl_match = re.search(r"\bimplements\s+([^\{\n]+)", tail)
    if impl_match:
        implements_list = split_types_list(impl_match.group(1))

    return {
        "type_name": name,
        "type_kind": type_kind,
        "extends": extends_list,
        "implements": implements_list,
    }


def method_name_and_params(method_sig):
    sig = re.sub(r"\bthrows\b.*$", "", method_sig).strip()
    match = re.search(r"([A-Za-z_]\w*)\s*\((.*)\)\s*$", sig)
    if not match:
        return None, []

    method_name = match.group(1)
    params_text = match.group(2).strip()
    params = split_params(params_text)
    param_types = [clean_param_type(param) for param in params if param.strip()]
    return method_name, [p for p in param_types if p]


def extract_private_field_names(lines):
    private_fields = set()
    for line in lines:
        stripped = line.strip()
        if not stripped or stripped.startswith("//"):
            continue
        field_match = re.match(r"^private\s+[^();]+?\s+(\w+)\s*(?:=|;)", stripped)
        if field_match:
            private_fields.add(field_match.group(1))
    return private_fields


def kpis_for_method(context):
    kpis = []

    if context["has_inheritance"]:
        kpis.append(KPI_INHERITANCE)

    if context["has_encapsulation"]:
        kpis.append(KPI_ENCAPSULATION)

    if context["type_kind"] == "Class":
        kpis.append(KPI_CUSTOM_CLASS)
    elif context["type_kind"] == "Abstract Class":
        kpis.append(KPI_CUSTOM_ABSTRACT_CLASS)
    elif context["type_kind"] == "Interface":
        kpis.append(KPI_CUSTOM_INTERFACE)

    if context["is_override"] or context["is_overload"]:
        kpis.append(KPI_OVERRIDE_OVERLOAD)

    if any(param in context["custom_classes"] for param in context["param_types"]):
        kpis.append(KPI_PARAM_CUSTOM_CLASS)
    if any(param in context["custom_abstract_classes"] for param in context["param_types"]):
        kpis.append(KPI_PARAM_CUSTOM_ABSTRACT_CLASS)
    if any(param in context["custom_interfaces"] for param in context["param_types"]):
        kpis.append(KPI_PARAM_CUSTOM_INTERFACE)

    # Keep order and remove duplicates.
    unique = []
    for item in kpis:
        if item not in unique:
            unique.append(item)
    return unique


java_files = gather_java_files(source_dir)
custom_classes, custom_abstract_classes, custom_interfaces = build_custom_type_index(java_files)
all_custom_types = custom_classes | custom_abstract_classes | custom_interfaces

with open(output_csv, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["Owner", "Type", "Path", "Method", "KPI", "Rate", "Note"])

    for file_path in java_files:
        file_name = os.path.basename(file_path)

        with open(file_path, "r", encoding="utf-8") as file_obj:
            content = file_obj.read()

        type_header = extract_type_header(content)
        if not type_header:
            continue

        inheritance_parents = set(type_header["extends"] + type_header["implements"])
        has_inheritance = any(parent in all_custom_types for parent in inheritance_parents)

        lines = content.split("\n")
        private_fields = extract_private_field_names(lines)

        comment_buffer = []
        annotation_buffer = []
        in_block_comment = False
        methods = []

        for line in lines:
            stripped_line = line.strip()

            if stripped_line.startswith("/*"):
                in_block_comment = True
                comment_buffer.append(stripped_line)
                if "*/" in stripped_line:
                    in_block_comment = False
                continue

            if in_block_comment:
                comment_buffer.append(stripped_line)
                if "*/" in stripped_line:
                    in_block_comment = False
                continue

            if stripped_line.startswith("//"):
                comment_buffer.append(stripped_line)
                continue

            if not stripped_line:
                continue

            if stripped_line.startswith("@"):
                annotation_buffer.append(stripped_line)
                continue

            if stripped_line.endswith("{") and "(" in stripped_line:
                method_sig = stripped_line[:-1].strip()

                bad_keywords = [
                    "if (", "if(", "for (", "for(", "while (", "while(",
                    "catch (", "catch(", "switch (", "switch(", "try (", "try(",
                    "class ", "interface ", "enum "
                ]

                is_valid_method = (
                    not any(keyword in method_sig for keyword in bad_keywords)
                    and "->" not in method_sig
                    and "=" not in method_sig
                    and "&&" not in method_sig
                    and "||" not in method_sig
                )

                if is_valid_method:
                    method_name, param_types = method_name_and_params(method_sig)
                    methods.append(
                        {
                            "method_sig": method_sig,
                            "method_name": method_name,
                            "param_types": param_types,
                            "note_text": "\n".join(comment_buffer),
                            "is_override": any(
                                ann.startswith("@Override") for ann in annotation_buffer
                            ),
                        }
                    )

                comment_buffer = []
                annotation_buffer = []
                continue

            comment_buffer = []
            annotation_buffer = []

        method_names = [m["method_name"] for m in methods if m["method_name"]]
        overload_candidates = {
            name for name in method_names if method_names.count(name) > 1
        }

        method_sigs = [m["method_sig"] for m in methods]
        has_public_or_protected_method = any(
            sig.startswith("public ") or sig.startswith("protected ") for sig in method_sigs
        )

        has_getter_or_setter = False
        if private_fields:
            expected_prefixes = set()
            for field in private_fields:
                cap = field[:1].upper() + field[1:]
                expected_prefixes.update({f"get{cap}", f"set{cap}", f"is{cap}"})
            has_getter_or_setter = any(name in expected_prefixes for name in method_names)

        has_encapsulation = (
            type_header["type_kind"] in {"Class", "Abstract Class"}
            and bool(private_fields)
            and (has_getter_or_setter or has_public_or_protected_method)
        )

        for method in methods:
            context = {
                "type_kind": type_header["type_kind"],
                "has_inheritance": has_inheritance,
                "has_encapsulation": has_encapsulation,
                "is_override": method["is_override"],
                "is_overload": method["method_name"] in overload_candidates,
                "param_types": method["param_types"],
                "custom_classes": custom_classes,
                "custom_abstract_classes": custom_abstract_classes,
                "custom_interfaces": custom_interfaces,
            }

            kpi_text = " ; ".join(kpis_for_method(context))

            writer.writerow(
                [
                    "Custom",
                    type_header["type_kind"],
                    file_name,
                    method["method_sig"],
                    kpi_text,
                    "",
                    method["note_text"],
                ]
            )

print(f"Documentation generated in {output_csv}")