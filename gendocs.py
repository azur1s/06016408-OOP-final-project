'''older version of gendocV2.py'''
import os
import re
import csv
from datetime import datetime

# Format time to be "2024-06-01 12:00:00"
current_time = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")

source_dir = "src"
output_csv = f"docs_{current_time}.csv"

# Match class, abstract class, interface or other type definitions
type_pattern = re.compile(r"\b(class|interface|abstract\s+class|enum)\s+(\w+)")

with open(output_csv, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["Owner", "Type", "Path", "Method Signature", "Requirements", "Rate", "Note"])

    for root, dirs, files in os.walk(source_dir):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)

                with open(file_path, "r", encoding="utf-8") as f:
                    content = f.read()

                    # Determine Type (Default to Class if not found)
                    type_match = type_pattern.search(content)
                    java_type = type_match.group(1).capitalize() if type_match else 'Class'

                    # Read lines to extract methods and their preceding comments
                    lines = content.split('\n')

                    comment_buffer = []
                    in_block_comment = False

                    for line in lines:
                        stripped_line = line.strip()

                        # Handle Block Comments (/** ... */ or /* ... */)
                        if stripped_line.startswith('/*'):
                            in_block_comment = True
                            comment_buffer.append(stripped_line)
                            if '*/' in stripped_line:
                                in_block_comment = False
                            continue

                        if in_block_comment:
                            comment_buffer.append(stripped_line)
                            if '*/' in stripped_line:
                                in_block_comment = False
                            continue

                        # Handle Line Comments (// or ///)
                        if stripped_line.startswith('//'):
                            comment_buffer.append(stripped_line)
                            continue

                        # Ignore empty lines and annotations (@)
                        # This ensures the buffer isn't cleared if there is a gap or an @Override
                        # between the comment and the method.
                        if not stripped_line or stripped_line.startswith('@'):
                            continue

                        # Method detection
                        if stripped_line.endswith('{') and '(' in stripped_line:
                            method_sig = stripped_line.replace('{', '').strip()

                            # Expanded list to catch try-with-resources and formatting variations
                            bad_keywords = [
                                'if (', 'if(', 'for (', 'for(', 'while (', 'while(',
                                'catch (', 'catch(', 'switch (', 'switch(', 'try (', 'try(',
                                'class ', 'interface ', 'enum '
                            ]

                            # Exclude lines that contain lambdas, assignments, or boolean logic
                            is_valid_method = (
                                not any(keyword in method_sig for keyword in bad_keywords)
                                and '->' not in method_sig
                                and '=' not in method_sig
                                and '&&' not in method_sig
                                and '||' not in method_sig
                            )

                            if is_valid_method:
                                # Join the collected comments with line breaks
                                note_text = '\n'.join(comment_buffer)

                                # Write the row including the note_text
                                writer.writerow(['Custom', java_type, file, method_sig, '', '', note_text])

                        # If the line was code (not a comment, empty, annotation, or method declaration),
                        # we clear the buffer so older comments don't attach to the next method.
                        comment_buffer = []

print(f"Documentation generated in {output_csv}")