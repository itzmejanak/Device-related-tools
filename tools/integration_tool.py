#!/usr/bin/env python3
"""
Integration Tool for Manufacturer Updates
Tool to safely integrate reviewed changes into the current codebase
"""

import os
import sys
import zipfile
import tempfile
import shutil
import json
from pathlib import Path
from datetime import datetime

class IntegrationTool:
    def __init__(self, current_path, zip_path):
        self.current_path = Path(current_path)
        self.zip_path = Path(zip_path)
        self.temp_dir = None
        self.extracted_path = None
        self.backup_dir = None
        self.state_file = "review_state.json"
        
    def extract_zip(self):
        """Extract manufacturer's zip file"""
        if self.temp_dir and os.path.exists(self.temp_dir):
            return True
            
        self.temp_dir = tempfile.mkdtemp(prefix="integration_")
        
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
    
    def create_backup(self):
        """Create backup of current codebase"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.backup_dir = Path(f"backup_device_util_{timestamp}")
        
        try:
            shutil.copytree(self.current_path, self.backup_dir, 
                          ignore=shutil.ignore_patterns('target', '.idea', '__pycache__', '*.class'))
            print(f"✅ Backup created: {self.backup_dir}")
            return True
        except Exception as e:
            print(f"❌ Error creating backup: {e}")
            return False
    
    def load_review_state(self):
        """Load review state from previous session"""
        if os.path.exists(self.state_file):
            try:
                with open(self.state_file, 'r') as f:
                    return json.load(f)
            except:
                return {}
        return {}
    
    def get_files_to_integrate(self):
        """Get list of files marked for integration"""
        review_state = self.load_review_state()
        return [file_path for file_path, status in review_state.items() 
                if status == 'integrated']
    
    def integrate_file(self, file_path):
        """Integrate a single file"""
        source_file = self.extracted_path / file_path
        target_file = self.current_path / file_path
        
        try:
            # Create target directory if it doesn't exist
            target_file.parent.mkdir(parents=True, exist_ok=True)
            
            # Copy file
            shutil.copy2(source_file, target_file)
            print(f"✅ Integrated: {file_path}")
            return True
            
        except Exception as e:
            print(f"❌ Error integrating {file_path}: {e}")
            return False
    
    def show_integration_plan(self):
        """Show what will be integrated"""
        files_to_integrate = self.get_files_to_integrate()
        
        if not files_to_integrate:
            print("❌ No files marked for integration")
            print("💡 Use the code_review_tool.py first to review and mark files")
            return False
        
        print(f"\n📋 INTEGRATION PLAN")
        print("=" * 50)
        print(f"Files to integrate: {len(files_to_integrate)}")
        print()
        
        # Categorize files
        categories = {
            'Database': [],
            'Controllers': [],
            'Services': [],
            'Entities': [],
            'Configuration': [],
            'Other': []
        }
        
        for file_path in files_to_integrate:
            if 'entity' in file_path.lower() or 'Entity' in file_path:
                categories['Database'].append(file_path)
            elif 'Controller' in file_path:
                categories['Controllers'].append(file_path)
            elif 'Service' in file_path or 'service' in file_path:
                categories['Services'].append(file_path)
            elif file_path.endswith('.sql'):
                categories['Database'].append(file_path)
            elif file_path.endswith(('.xml', '.yml', '.yaml')):
                categories['Configuration'].append(file_path)
            else:
                categories['Other'].append(file_path)
        
        for category, files in categories.items():
            if files:
                print(f"📁 {category}:")
                for file_path in files:
                    print(f"   • {file_path}")
                print()
        
        return True
    
    def integrate_all(self):
        """Integrate all marked files"""
        if not self.extract_zip():
            return False
        
        if not self.show_integration_plan():
            return False
        
        # Confirm integration
        response = input("\n❓ Proceed with integration? (y/N): ").strip().lower()
        if response != 'y':
            print("❌ Integration cancelled")
            return False
        
        # Create backup
        if not self.create_backup():
            return False
        
        # Integrate files
        files_to_integrate = self.get_files_to_integrate()
        success_count = 0
        
        print(f"\n🔄 Integrating {len(files_to_integrate)} files...")
        
        for file_path in files_to_integrate:
            if self.integrate_file(file_path):
                success_count += 1
        
        print(f"\n📊 Integration Results:")
        print(f"   • Successfully integrated: {success_count}")
        print(f"   • Failed: {len(files_to_integrate) - success_count}")
        print(f"   • Backup location: {self.backup_dir}")
        
        if success_count == len(files_to_integrate):
            print("✅ All files integrated successfully!")
            return True
        else:
            print("⚠️  Some files failed to integrate")
            return False
    
    def extract_specific_file(self, file_path, output_path=None):
        """Extract a specific file for manual review"""
        if not self.extract_zip():
            return False
        
        source_file = self.extracted_path / file_path
        if not source_file.exists():
            print(f"❌ File not found: {file_path}")
            return False
        
        if output_path is None:
            output_path = f"extracted_{file_path.replace('/', '_')}"
        
        try:
            shutil.copy2(source_file, output_path)
            print(f"✅ Extracted to: {output_path}")
            return True
        except Exception as e:
            print(f"❌ Error extracting file: {e}")
            return False
    
    def show_file_content(self, file_path):
        """Show content of a specific file from manufacturer's version"""
        if not self.extract_zip():
            return False
        
        source_file = self.extracted_path / file_path
        if not source_file.exists():
            print(f"❌ File not found: {file_path}")
            return False
        
        try:
            with open(source_file, 'r', encoding='utf-8') as f:
                content = f.read()
            
            print(f"\n📄 CONTENT: {file_path}")
            print("=" * 80)
            
            lines = content.splitlines()
            for i, line in enumerate(lines, 1):
                print(f"{i:4d}: {line}")
            
            return True
            
        except Exception as e:
            print(f"❌ Error reading file: {e}")
            return False
    
    def interactive_mode(self):
        """Interactive integration mode"""
        print("🔧 INTEGRATION TOOL")
        print("=" * 50)
        
        while True:
            print(f"\n🎯 COMMANDS:")
            print("  plan     - Show integration plan")
            print("  integrate - Integrate all marked files")
            print("  extract <file> - Extract specific file")
            print("  show <file> - Show file content")
            print("  backup   - Create backup only")
            print("  quit     - Exit")
            
            try:
                cmd = input("\n👉 Enter command: ").strip()
                
                if cmd == 'quit':
                    break
                elif cmd == 'plan':
                    self.show_integration_plan()
                elif cmd == 'integrate':
                    self.integrate_all()
                elif cmd == 'backup':
                    self.create_backup()
                elif cmd.startswith('extract '):
                    file_path = cmd[8:].strip()
                    self.extract_specific_file(file_path)
                elif cmd.startswith('show '):
                    file_path = cmd[5:].strip()
                    self.show_file_content(file_path)
                else:
                    print("❌ Unknown command")
                    
            except KeyboardInterrupt:
                print("\n\n👋 Goodbye!")
                break
    
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
    
    tool = IntegrationTool(current_path, zip_path)
    
    try:
        tool.interactive_mode()
    except KeyboardInterrupt:
        print("\n\n👋 Integration session ended")
    finally:
        tool.cleanup()
    
    return 0

if __name__ == "__main__":
    sys.exit(main())