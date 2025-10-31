#!/usr/bin/env python3
"""
Code Review Tool for Manufacturer Updates
Interactive tool to review, compare, and integrate manufacturer changes
"""

import os
import sys
import zipfile
import tempfile
import shutil
import difflib
from pathlib import Path
import json
from datetime import datetime
import subprocess

class CodeReviewTool:
    def __init__(self, current_path, zip_path):
        self.current_path = Path(current_path)
        self.zip_path = Path(zip_path)
        self.temp_dir = None
        self.extracted_path = None
        self.review_state = {}
        self.state_file = "review_state.json"
        
        # Load previous state if exists
        self.load_state()
        
    def load_state(self):
        """Load previous review state"""
        if os.path.exists(self.state_file):
            try:
                with open(self.state_file, 'r') as f:
                    self.review_state = json.load(f)
                print(f"📋 Loaded previous review state: {len(self.review_state)} files reviewed")
            except:
                self.review_state = {}
        else:
            self.review_state = {}
    
    def save_state(self):
        """Save current review state"""
        with open(self.state_file, 'w') as f:
            json.dump(self.review_state, f, indent=2)
    
    def extract_zip(self):
        """Extract manufacturer's zip file"""
        if self.temp_dir and os.path.exists(self.temp_dir):
            return True
            
        self.temp_dir = tempfile.mkdtemp(prefix="code_review_")
        
        try:
            with zipfile.ZipFile(self.zip_path, 'r') as zip_ref:
                zip_ref.extractall(self.temp_dir)
            
            # Find extracted directory
            extracted_items = list(Path(self.temp_dir).iterdir())
            if len(extracted_items) == 1 and extracted_items[0].is_dir():
                self.extracted_path = extracted_items[0]
            else:
                self.extracted_path = Path(self.temp_dir)
                
            print(f"✅ Extracted to: {self.extracted_path}")
            return True
            
        except Exception as e:
            print(f"❌ Error extracting zip: {e}")
            return False
    
    def get_file_changes(self):
        """Get categorized file changes"""
        if not self.extract_zip():
            return None
            
        # Get file trees
        current_files = set()
        manufacturer_files = set()
        
        # Current files
        for file_path in self.current_path.rglob('*'):
            if file_path.is_file() and not any(ignore in file_path.parts for ignore in ['target', '.idea', '__pycache__']):
                current_files.add(str(file_path.relative_to(self.current_path)))
        
        # Manufacturer files
        for file_path in self.extracted_path.rglob('*'):
            if file_path.is_file() and not any(ignore in file_path.parts for ignore in ['target', '.idea', '__pycache__']):
                manufacturer_files.add(str(file_path.relative_to(self.extracted_path)))
        
        # Categorize changes
        added_files = manufacturer_files - current_files
        removed_files = current_files - manufacturer_files
        common_files = current_files & manufacturer_files
        
        # Check for modifications
        modified_files = []
        for file_path in common_files:
            current_file = self.current_path / file_path
            manufacturer_file = self.extracted_path / file_path
            
            if current_file.exists() and manufacturer_file.exists():
                try:
                    with open(current_file, 'rb') as f1, open(manufacturer_file, 'rb') as f2:
                        if f1.read() != f2.read():
                            modified_files.append(file_path)
                except:
                    pass
        
        return {
            'added': sorted(added_files),
            'removed': sorted(removed_files),
            'modified': sorted(modified_files),
            'unchanged': sorted(common_files - set(modified_files))
        }
    
    def read_file_safe(self, file_path):
        """Read file content safely"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return f.read()
        except UnicodeDecodeError:
            try:
                with open(file_path, 'r', encoding='latin-1') as f:
                    return f.read()
            except:
                return "[Binary file - cannot display]"
        except Exception as e:
            return f"[Error reading file: {e}]"
    
    def show_file_diff(self, file_path):
        """Show detailed diff for a file"""
        current_file = self.current_path / file_path
        manufacturer_file = self.extracted_path / file_path
        
        print(f"\n{'='*80}")
        print(f"📄 FILE: {file_path}")
        print(f"{'='*80}")
        
        if not current_file.exists():
            print("🆕 NEW FILE (Added by manufacturer)")
            content = self.read_file_safe(manufacturer_file)
            print(f"\n📝 Content ({len(content.splitlines())} lines):")
            print("-" * 40)
            for i, line in enumerate(content.splitlines()[:50], 1):
                print(f"{i:3d}: {line}")
            if len(content.splitlines()) > 50:
                print(f"... ({len(content.splitlines()) - 50} more lines)")
        
        elif not manufacturer_file.exists():
            print("🗑️  REMOVED FILE")
            
        else:
            # Show diff
            current_content = self.read_file_safe(current_file).splitlines(keepends=True)
            manufacturer_content = self.read_file_safe(manufacturer_file).splitlines(keepends=True)
            
            diff = list(difflib.unified_diff(
                current_content, manufacturer_content,
                fromfile=f"current/{file_path}",
                tofile=f"manufacturer/{file_path}",
                lineterm=''
            ))
            
            if diff:
                print("🔄 MODIFIED FILE")
                print(f"\n📊 Stats:")
                added_lines = sum(1 for line in diff if line.startswith('+') and not line.startswith('+++'))
                removed_lines = sum(1 for line in diff if line.startswith('-') and not line.startswith('---'))
                print(f"  • Added lines: {added_lines}")
                print(f"  • Removed lines: {removed_lines}")
                
                print(f"\n📝 Diff:")
                print("-" * 40)
                for line in diff[:100]:  # Show first 100 lines
                    print(line.rstrip())
                if len(diff) > 100:
                    print(f"... ({len(diff) - 100} more lines)")
            else:
                print("✅ FILES ARE IDENTICAL")
    
    def analyze_java_file(self, file_path):
        """Analyze Java file for important changes"""
        manufacturer_file = self.extracted_path / file_path
        content = self.read_file_safe(manufacturer_file)
        
        analysis = {
            'imports': [],
            'classes': [],
            'methods': [],
            'annotations': [],
            'database_related': False,
            'binding_related': False
        }
        
        lines = content.splitlines()
        for line in lines:
            line = line.strip()
            
            if line.startswith('import '):
                analysis['imports'].append(line)
                
            if any(keyword in line for keyword in ['@Entity', '@Table', '@Repository', '@Service']):
                analysis['annotations'].append(line)
                analysis['database_related'] = True
                
            if 'binding' in line.lower() or 'cabinet' in line.lower():
                analysis['binding_related'] = True
                
            if line.startswith('public class ') or line.startswith('public interface '):
                analysis['classes'].append(line)
                
            if any(modifier in line for modifier in ['public ', 'private ', 'protected ']):
                if '(' in line and ')' in line and not line.startswith('//'):
                    analysis['methods'].append(line)
        
        return analysis
    
    def show_file_analysis(self, file_path):
        """Show detailed analysis of a file"""
        if file_path.endswith('.java'):
            analysis = self.analyze_java_file(file_path)
            
            print(f"\n🔍 JAVA FILE ANALYSIS:")
            print("-" * 40)
            
            if analysis['database_related']:
                print("🗄️  DATABASE RELATED: Yes")
            if analysis['binding_related']:
                print("🔗 BINDING RELATED: Yes")
                
            if analysis['imports']:
                print(f"\n📦 Key Imports ({len(analysis['imports'])}):")
                for imp in analysis['imports'][:10]:
                    print(f"  • {imp}")
                if len(analysis['imports']) > 10:
                    print(f"  ... and {len(analysis['imports']) - 10} more")
            
            if analysis['classes']:
                print(f"\n🏗️  Classes/Interfaces:")
                for cls in analysis['classes']:
                    print(f"  • {cls}")
            
            if analysis['annotations']:
                print(f"\n📝 Key Annotations:")
                for ann in analysis['annotations']:
                    print(f"  • {ann}")
        
        elif file_path.endswith('.sql'):
            content = self.read_file_safe(self.extracted_path / file_path)
            print(f"\n🗄️  SQL FILE ANALYSIS:")
            print("-" * 40)
            
            # Count SQL statements
            create_tables = content.upper().count('CREATE TABLE')
            inserts = content.upper().count('INSERT INTO')
            
            print(f"📊 SQL Statistics:")
            print(f"  • CREATE TABLE statements: {create_tables}")
            print(f"  • INSERT statements: {inserts}")
            print(f"  • Total lines: {len(content.splitlines())}")
            
        elif file_path.endswith(('.xml', '.yml', '.yaml')):
            content = self.read_file_safe(self.extracted_path / file_path)
            print(f"\n⚙️  CONFIG FILE ANALYSIS:")
            print("-" * 40)
            print(f"📊 File size: {len(content)} characters")
            print(f"📊 Lines: {len(content.splitlines())}")
    
    def get_file_priority(self, file_path):
        """Get priority score for file (higher = more important)"""
        priority = 0
        
        # High priority files
        if 'BindingController' in file_path:
            priority += 100
        if file_path.endswith('.sql'):
            priority += 90
        if 'Entity' in file_path or 'entity' in file_path:
            priority += 80
        if 'Service' in file_path or 'Repository' in file_path:
            priority += 70
        if 'pom.xml' in file_path:
            priority += 60
        if file_path.endswith('.yml') or file_path.endswith('.yaml'):
            priority += 50
        
        # Medium priority
        if file_path.endswith('.java'):
            priority += 30
        
        return priority
    
    def interactive_review(self):
        """Interactive review mode"""
        changes = self.get_file_changes()
        if not changes:
            return
        
        print(f"\n🔍 CODE REVIEW TOOL")
        print("=" * 50)
        print(f"📊 Summary:")
        print(f"  • Added: {len(changes['added'])} files")
        print(f"  • Modified: {len(changes['modified'])} files")
        print(f"  • Removed: {len(changes['removed'])} files")
        print(f"  • Unchanged: {len(changes['unchanged'])} files")
        
        # Combine and prioritize files to review
        files_to_review = []
        
        for file_path in changes['added']:
            files_to_review.append(('added', file_path, self.get_file_priority(file_path)))
        
        for file_path in changes['modified']:
            files_to_review.append(('modified', file_path, self.get_file_priority(file_path)))
        
        # Sort by priority (highest first)
        files_to_review.sort(key=lambda x: x[2], reverse=True)
        
        print(f"\n📋 Files to review (sorted by priority):")
        for i, (change_type, file_path, priority) in enumerate(files_to_review[:20], 1):
            status = self.review_state.get(file_path, 'pending')
            status_icon = {'pending': '⏳', 'reviewed': '✅', 'skipped': '⏭️', 'integrated': '🔄'}[status]
            print(f"{i:2d}. {status_icon} [{change_type.upper()}] {file_path} (priority: {priority})")
        
        if len(files_to_review) > 20:
            print(f"    ... and {len(files_to_review) - 20} more files")
        
        while True:
            print(f"\n🎯 COMMANDS:")
            print("  r <num>  - Review file by number")
            print("  l        - List files again")
            print("  s        - Show summary")
            print("  q        - Quit")
            print("  h        - Help")
            
            try:
                cmd = input("\n👉 Enter command: ").strip().lower()
                
                if cmd == 'q':
                    break
                elif cmd == 'h':
                    self.show_help()
                elif cmd == 'l':
                    self.show_file_list(files_to_review)
                elif cmd == 's':
                    self.show_summary(changes)
                elif cmd.startswith('r '):
                    try:
                        file_num = int(cmd.split()[1]) - 1
                        if 0 <= file_num < len(files_to_review):
                            change_type, file_path, priority = files_to_review[file_num]
                            self.review_single_file(file_path, change_type)
                        else:
                            print(f"❌ Invalid file number. Use 1-{len(files_to_review)}")
                    except (ValueError, IndexError):
                        print("❌ Invalid command format. Use: r <number>")
                else:
                    print("❌ Unknown command. Type 'h' for help.")
                    
            except KeyboardInterrupt:
                print("\n\n👋 Goodbye!")
                break
        
        self.save_state()
    
    def review_single_file(self, file_path, change_type):
        """Review a single file interactively"""
        print(f"\n{'='*80}")
        print(f"📄 REVIEWING: {file_path}")
        print(f"📝 Change Type: {change_type.upper()}")
        print(f"{'='*80}")
        
        # Show file content/diff
        self.show_file_diff(file_path)
        
        # Show analysis
        self.show_file_analysis(file_path)
        
        # Get user decision
        while True:
            print(f"\n🎯 ACTIONS:")
            print("  v - View full file content")
            print("  d - Show detailed diff")
            print("  i - Mark as integrated")
            print("  s - Skip for now")
            print("  r - Mark as reviewed")
            print("  b - Back to main menu")
            
            action = input("\n👉 Action: ").strip().lower()
            
            if action == 'b':
                break
            elif action == 'v':
                self.view_full_file(file_path)
            elif action == 'd':
                self.show_detailed_diff(file_path)
            elif action in ['i', 's', 'r']:
                status_map = {'i': 'integrated', 's': 'skipped', 'r': 'reviewed'}
                self.review_state[file_path] = status_map[action]
                print(f"✅ Marked as {status_map[action]}")
                break
            else:
                print("❌ Invalid action")
    
    def view_full_file(self, file_path):
        """View full file content"""
        manufacturer_file = self.extracted_path / file_path
        content = self.read_file_safe(manufacturer_file)
        
        print(f"\n📄 FULL CONTENT: {file_path}")
        print("-" * 80)
        
        lines = content.splitlines()
        for i, line in enumerate(lines, 1):
            print(f"{i:4d}: {line}")
        
        input("\nPress Enter to continue...")
    
    def show_detailed_diff(self, file_path):
        """Show detailed diff with more context"""
        current_file = self.current_path / file_path
        manufacturer_file = self.extracted_path / file_path
        
        if current_file.exists() and manufacturer_file.exists():
            # Use external diff tool if available
            try:
                subprocess.run(['diff', '-u', str(current_file), str(manufacturer_file)])
            except:
                # Fallback to internal diff
                self.show_file_diff(file_path)
        else:
            self.show_file_diff(file_path)
        
        input("\nPress Enter to continue...")
    
    def show_help(self):
        """Show help information"""
        print(f"\n📚 HELP")
        print("-" * 40)
        print("This tool helps you review manufacturer code changes systematically.")
        print("\nFile Status Icons:")
        print("  ⏳ Pending - Not reviewed yet")
        print("  ✅ Reviewed - Reviewed but not integrated")
        print("  🔄 Integrated - Changes have been integrated")
        print("  ⏭️  Skipped - Skipped for now")
        print("\nPriority System:")
        print("  • 100+ Critical files (BindingController, etc.)")
        print("  • 90+  Database files (SQL, entities)")
        print("  • 70+  Service/Repository files")
        print("  • 50+  Configuration files")
        print("  • 30+  Other Java files")
    
    def show_file_list(self, files_to_review):
        """Show file list with current status"""
        print(f"\n📋 FILES TO REVIEW:")
        print("-" * 80)
        
        for i, (change_type, file_path, priority) in enumerate(files_to_review, 1):
            status = self.review_state.get(file_path, 'pending')
            status_icon = {'pending': '⏳', 'reviewed': '✅', 'skipped': '⏭️', 'integrated': '🔄'}[status]
            print(f"{i:2d}. {status_icon} [{change_type.upper():8s}] {file_path}")
    
    def show_summary(self, changes):
        """Show review summary"""
        total_files = len(changes['added']) + len(changes['modified'])
        reviewed = sum(1 for status in self.review_state.values() if status in ['reviewed', 'integrated'])
        
        print(f"\n📊 REVIEW SUMMARY:")
        print("-" * 40)
        print(f"Total files to review: {total_files}")
        print(f"Files reviewed: {reviewed}")
        print(f"Progress: {reviewed/total_files*100:.1f}%" if total_files > 0 else "Progress: 0%")
        
        # Status breakdown
        status_counts = {}
        for status in self.review_state.values():
            status_counts[status] = status_counts.get(status, 0) + 1
        
        if status_counts:
            print(f"\nStatus breakdown:")
            for status, count in status_counts.items():
                print(f"  • {status.title()}: {count}")
    
    def cleanup(self):
        """Clean up temporary files"""
        if self.temp_dir and os.path.exists(self.temp_dir):
            shutil.rmtree(self.temp_dir)

def main():
    current_path = "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools/device-util-demo"
    zip_path = "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools/device-util-demo.zip"
    
    if not os.path.exists(current_path):
        print(f"❌ Current path not found: {current_path}")
        return 1
    
    if not os.path.exists(zip_path):
        print(f"❌ Zip file not found: {zip_path}")
        return 1
    
    tool = CodeReviewTool(current_path, zip_path)
    
    try:
        tool.interactive_review()
    except KeyboardInterrupt:
        print("\n\n👋 Review session ended")
    finally:
        tool.cleanup()
    
    return 0

if __name__ == "__main__":
    sys.exit(main())