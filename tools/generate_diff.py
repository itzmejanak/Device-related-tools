#!/usr/bin/env python3
"""
Device Util Demo Diff Generator
Compares the current device-util-demo with the updated version from manufacturer
Generates a comprehensive diff report in Markdown format
"""

import os
import sys
import zipfile
import tempfile
import shutil
import difflib
from pathlib import Path
import hashlib
from datetime import datetime

class DeviceUtilDiffGenerator:
    def __init__(self, current_path, zip_path, output_file="MANUFACTURER_DIFF_REPORT.md"):
        self.current_path = Path(current_path)
        self.zip_path = Path(zip_path)
        self.output_file = output_file
        self.temp_dir = None
        self.extracted_path = None
        
        # File extensions to analyze
        self.code_extensions = {'.java', '.xml', '.yml', '.yaml', '.properties', '.sql', '.md'}
        self.ignore_dirs = {'target', '.idea', '__pycache__', '.git', 'node_modules'}
        
    def extract_zip(self):
        """Extract the manufacturer's zip file to temporary directory"""
        self.temp_dir = tempfile.mkdtemp(prefix="device_util_diff_")
        
        try:
            with zipfile.ZipFile(self.zip_path, 'r') as zip_ref:
                zip_ref.extractall(self.temp_dir)
            
            # Find the extracted directory (might be nested)
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
    
    def get_file_hash(self, file_path):
        """Get MD5 hash of file content"""
        try:
            with open(file_path, 'rb') as f:
                return hashlib.md5(f.read()).hexdigest()
        except:
            return None
    
    def get_file_tree(self, root_path):
        """Get a dictionary of all files with their relative paths and hashes"""
        file_tree = {}
        
        for file_path in root_path.rglob('*'):
            if file_path.is_file():
                # Skip ignored directories
                if any(ignore_dir in file_path.parts for ignore_dir in self.ignore_dirs):
                    continue
                
                rel_path = file_path.relative_to(root_path)
                file_hash = self.get_file_hash(file_path)
                
                file_tree[str(rel_path)] = {
                    'path': file_path,
                    'hash': file_hash,
                    'size': file_path.stat().st_size if file_path.exists() else 0
                }
        
        return file_tree
    
    def read_file_content(self, file_path):
        """Read file content safely"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                return f.readlines()
        except UnicodeDecodeError:
            try:
                with open(file_path, 'r', encoding='latin-1') as f:
                    return f.readlines()
            except:
                return ["[Binary file - cannot display content]\n"]
        except Exception as e:
            return [f"[Error reading file: {e}]\n"]
    
    def generate_file_diff(self, file1_path, file2_path, rel_path):
        """Generate diff for a single file"""
        content1 = self.read_file_content(file1_path)
        content2 = self.read_file_content(file2_path)
        
        diff = list(difflib.unified_diff(
            content1, content2,
            fromfile=f"current/{rel_path}",
            tofile=f"manufacturer/{rel_path}",
            lineterm=''
        ))
        
        return diff
    
    def analyze_java_changes(self, diff_lines):
        """Analyze Java-specific changes"""
        changes = {
            'imports': [],
            'classes': [],
            'methods': [],
            'annotations': [],
            'database': [],
            'other': []
        }
        
        for line in diff_lines:
            if line.startswith('+') and not line.startswith('+++'):
                line_content = line[1:].strip()
                
                if line_content.startswith('import '):
                    changes['imports'].append(line_content)
                elif '@Entity' in line_content or '@Table' in line_content or '@Repository' in line_content:
                    changes['database'].append(line_content)
                elif line_content.startswith('@'):
                    changes['annotations'].append(line_content)
                elif 'class ' in line_content or 'interface ' in line_content:
                    changes['classes'].append(line_content)
                elif any(keyword in line_content for keyword in ['public ', 'private ', 'protected ']):
                    if '(' in line_content and ')' in line_content:
                        changes['methods'].append(line_content)
                    else:
                        changes['other'].append(line_content)
                else:
                    changes['other'].append(line_content)
        
        return changes
    
    def generate_report(self):
        """Generate the comprehensive diff report"""
        if not self.extract_zip():
            return False
        
        print("🔍 Analyzing file differences...")
        
        # Get file trees
        current_files = self.get_file_tree(self.current_path)
        manufacturer_files = self.get_file_tree(self.extracted_path)
        
        # Find differences
        added_files = set(manufacturer_files.keys()) - set(current_files.keys())
        removed_files = set(current_files.keys()) - set(manufacturer_files.keys())
        common_files = set(current_files.keys()) & set(manufacturer_files.keys())
        
        # Find modified files
        modified_files = []
        for file_path in common_files:
            if current_files[file_path]['hash'] != manufacturer_files[file_path]['hash']:
                modified_files.append(file_path)
        
        # Generate report
        with open(self.output_file, 'w', encoding='utf-8') as f:
            f.write(f"# Device Util Demo - Manufacturer Update Diff Report\n\n")
            f.write(f"**Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
            f.write(f"**Current Path:** `{self.current_path}`\n")
            f.write(f"**Manufacturer Zip:** `{self.zip_path}`\n\n")
            
            # Summary
            f.write("## 📊 Summary\n\n")
            f.write(f"- **Added Files:** {len(added_files)}\n")
            f.write(f"- **Removed Files:** {len(removed_files)}\n")
            f.write(f"- **Modified Files:** {len(modified_files)}\n")
            f.write(f"- **Unchanged Files:** {len(common_files) - len(modified_files)}\n\n")
            
            # Added Files
            if added_files:
                f.write("## ➕ Added Files\n\n")
                for file_path in sorted(added_files):
                    file_info = manufacturer_files[file_path]
                    f.write(f"- `{file_path}` ({file_info['size']} bytes)\n")
                f.write("\n")
            
            # Removed Files
            if removed_files:
                f.write("## ➖ Removed Files\n\n")
                for file_path in sorted(removed_files):
                    file_info = current_files[file_path]
                    f.write(f"- `{file_path}` ({file_info['size']} bytes)\n")
                f.write("\n")
            
            # Modified Files
            if modified_files:
                f.write("## 🔄 Modified Files\n\n")
                
                java_changes_summary = {
                    'database_related': [],
                    'binding_logic': [],
                    'new_entities': [],
                    'new_services': [],
                    'configuration': []
                }
                
                for file_path in sorted(modified_files):
                    f.write(f"### `{file_path}`\n\n")
                    
                    current_file = current_files[file_path]['path']
                    manufacturer_file = manufacturer_files[file_path]['path']
                    
                    # Generate diff
                    diff_lines = self.generate_file_diff(current_file, manufacturer_file, file_path)
                    
                    if file_path.endswith('.java'):
                        # Analyze Java changes
                        java_changes = self.analyze_java_changes(diff_lines)
                        
                        # Categorize changes
                        if 'binding' in file_path.lower() or 'device' in file_path.lower():
                            java_changes_summary['binding_logic'].append(file_path)
                        if any(db_keyword in str(java_changes) for db_keyword in ['@Entity', '@Table', '@Repository', 'JPA']):
                            java_changes_summary['database_related'].append(file_path)
                        if 'Entity' in file_path or 'Model' in file_path:
                            java_changes_summary['new_entities'].append(file_path)
                        if 'Service' in file_path or 'Repository' in file_path:
                            java_changes_summary['new_services'].append(file_path)
                        
                        # Show Java-specific changes
                        if java_changes['imports']:
                            f.write("**New Imports:**\n")
                            for imp in java_changes['imports'][:5]:  # Limit to first 5
                                f.write(f"- `{imp}`\n")
                            if len(java_changes['imports']) > 5:
                                f.write(f"- ... and {len(java_changes['imports']) - 5} more\n")
                            f.write("\n")
                        
                        if java_changes['database']:
                            f.write("**Database Annotations:**\n")
                            for db in java_changes['database']:
                                f.write(f"- `{db}`\n")
                            f.write("\n")
                        
                        if java_changes['classes']:
                            f.write("**New Classes/Interfaces:**\n")
                            for cls in java_changes['classes']:
                                f.write(f"- `{cls}`\n")
                            f.write("\n")
                    
                    # Show first 20 lines of diff for code files
                    if any(file_path.endswith(ext) for ext in self.code_extensions):
                        if len(diff_lines) > 0:
                            f.write("**Diff Preview:**\n")
                            f.write("```diff\n")
                            for line in diff_lines[:20]:  # First 20 lines
                                f.write(f"{line}\n")
                            if len(diff_lines) > 20:
                                f.write(f"... ({len(diff_lines) - 20} more lines)\n")
                            f.write("```\n\n")
                    
                    f.write("---\n\n")
                
                # Summary of Java Changes
                f.write("## 🔍 Java Changes Summary\n\n")
                
                if java_changes_summary['database_related']:
                    f.write("### Database-Related Changes\n")
                    for file_path in java_changes_summary['database_related']:
                        f.write(f"- `{file_path}`\n")
                    f.write("\n")
                
                if java_changes_summary['binding_logic']:
                    f.write("### Device Binding Logic\n")
                    for file_path in java_changes_summary['binding_logic']:
                        f.write(f"- `{file_path}`\n")
                    f.write("\n")
                
                if java_changes_summary['new_entities']:
                    f.write("### New Entities/Models\n")
                    for file_path in java_changes_summary['new_entities']:
                        f.write(f"- `{file_path}`\n")
                    f.write("\n")
                
                if java_changes_summary['new_services']:
                    f.write("### New Services/Repositories\n")
                    for file_path in java_changes_summary['new_services']:
                        f.write(f"- `{file_path}`\n")
                    f.write("\n")
            
            # Key Files to Review
            f.write("## 🎯 Key Files to Review\n\n")
            
            key_files = [
                'brezze-communication/src/main/java/com/brezze/share/communication/controller/BindingController.java',
                'brezze-communication/src/main/resources/application-docker.yml',
                'brezze-communication/pom.xml'
            ]
            
            for key_file in key_files:
                if key_file in modified_files:
                    f.write(f"- ✅ **Modified:** `{key_file}`\n")
                elif key_file in added_files:
                    f.write(f"- ➕ **Added:** `{key_file}`\n")
                elif key_file in current_files:
                    f.write(f"- ⚪ **Unchanged:** `{key_file}`\n")
                else:
                    f.write(f"- ❓ **Missing:** `{key_file}`\n")
            
            f.write("\n")
            
            # Recommendations
            f.write("## 💡 Recommendations\n\n")
            f.write("1. **Review Database Changes:** Check for new entities, repositories, and database schema\n")
            f.write("2. **Analyze Binding Logic:** Review changes to device binding implementation\n")
            f.write("3. **Update Dependencies:** Check for new Maven dependencies in pom.xml\n")
            f.write("4. **Test Integration:** Ensure new changes work with existing frontend tools\n")
            f.write("5. **Database Migration:** Plan database schema updates if needed\n\n")
            
            # Next Steps
            f.write("## 🚀 Next Steps\n\n")
            f.write("1. Review this diff report thoroughly\n")
            f.write("2. Backup current implementation\n")
            f.write("3. Merge manufacturer changes carefully\n")
            f.write("4. Update database schema if needed\n")
            f.write("5. Test all functionality\n")
            f.write("6. Update deployment scripts\n\n")
        
        print(f"✅ Diff report generated: {self.output_file}")
        return True
    
    def cleanup(self):
        """Clean up temporary files"""
        if self.temp_dir and os.path.exists(self.temp_dir):
            shutil.rmtree(self.temp_dir)
            print("🧹 Cleaned up temporary files")

def main():
    # Paths
    current_path = "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools/device-util-demo"
    zip_path = "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools/device-util-demo.zip"
    
    # Check if paths exist
    if not os.path.exists(current_path):
        print(f"❌ Current path not found: {current_path}")
        return 1
    
    if not os.path.exists(zip_path):
        print(f"❌ Zip file not found: {zip_path}")
        return 1
    
    print("🔍 Device Util Demo Diff Generator")
    print("=" * 50)
    print(f"Current: {current_path}")
    print(f"Manufacturer: {zip_path}")
    print()
    
    # Generate diff
    generator = DeviceUtilDiffGenerator(current_path, zip_path)
    
    try:
        success = generator.generate_report()
        return 0 if success else 1
    except KeyboardInterrupt:
        print("\n❌ Operation cancelled by user")
        return 1
    except Exception as e:
        print(f"❌ Error: {e}")
        return 1
    finally:
        generator.cleanup()

if __name__ == "__main__":
    sys.exit(main())