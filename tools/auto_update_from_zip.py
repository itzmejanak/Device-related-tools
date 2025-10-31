#!/usr/bin/env python3
"""
Auto Update from Manufacturer's Zip
Automatically updates the current codebase with manufacturer's changes
Then we can use git diff to review changes systematically
"""

import os
import sys
import zipfile
import tempfile
import shutil
from pathlib import Path
import subprocess
from datetime import datetime

class AutoUpdater:
    def __init__(self, current_path, zip_path):
        self.current_path = Path(current_path)
        self.zip_path = Path(zip_path)
        self.temp_dir = None
        self.extracted_path = None
        self.backup_created = False
        
    def create_git_backup(self):
        """Create a git commit as backup before updating"""
        try:
            # Check if we're in a git repository
            result = subprocess.run(['git', 'status'], 
                                  cwd=self.current_path, 
                                  capture_output=True, 
                                  text=True)
            
            if result.returncode != 0:
                print("⚠️  Not in a git repository. Creating backup directory instead...")
                return self.create_directory_backup()
            
            # Add all changes
            subprocess.run(['git', 'add', '.'], cwd=self.current_path, check=True)
            
            # Create backup commit
            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            commit_msg = f"Backup before manufacturer update - {timestamp}"
            
            result = subprocess.run(['git', 'commit', '-m', commit_msg], 
                                  cwd=self.current_path, 
                                  capture_output=True, 
                                  text=True)
            
            if result.returncode == 0:
                print(f"✅ Git backup created: {commit_msg}")
                self.backup_created = True
                return True
            else:
                print("ℹ️  No changes to commit, proceeding...")
                return True
                
        except subprocess.CalledProcessError as e:
            print(f"❌ Git backup failed: {e}")
            return self.create_directory_backup()
    
    def create_directory_backup(self):
        """Create a directory backup as fallback"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        backup_dir = Path(f"backup_before_update_{timestamp}")
        
        try:
            shutil.copytree(self.current_path, backup_dir, 
                          ignore=shutil.ignore_patterns('target', '.idea', '__pycache__', '*.class', '.git'))
            print(f"✅ Directory backup created: {backup_dir}")
            return True
        except Exception as e:
            print(f"❌ Directory backup failed: {e}")
            return False
    
    def extract_zip(self):
        """Extract manufacturer's zip file"""
        self.temp_dir = tempfile.mkdtemp(prefix="auto_update_")
        
        try:
            with zipfile.ZipFile(self.zip_path, 'r') as zip_ref:
                zip_ref.extractall(self.temp_dir)
            
            # Find extracted directory
            extracted_items = list(Path(self.temp_dir).iterdir())
            if len(extracted_items) == 1 and extracted_items[0].is_dir():
                self.extracted_path = extracted_items[0]
            else:
                self.extracted_path = Path(self.temp_dir)
                
            print(f"✅ Extracted manufacturer's code to: {self.extracted_path}")
            return True
            
        except Exception as e:
            print(f"❌ Error extracting zip: {e}")
            return False
    
    def get_files_to_update(self):
        """Get list of files that need to be updated"""
        files_to_update = []
        
        # Get all files from manufacturer's version
        for file_path in self.extracted_path.rglob('*'):
            if file_path.is_file():
                # Skip IDE and build files
                if any(ignore in file_path.parts for ignore in ['.idea', 'target', '__pycache__']):
                    continue
                
                rel_path = file_path.relative_to(self.extracted_path)
                files_to_update.append(rel_path)
        
        return sorted(files_to_update)
    
    def update_file(self, rel_path):
        """Update a single file"""
        source_file = self.extracted_path / rel_path
        target_file = self.current_path / rel_path
        
        try:
            # Create target directory if it doesn't exist
            target_file.parent.mkdir(parents=True, exist_ok=True)
            
            # Copy file
            shutil.copy2(source_file, target_file)
            return True
            
        except Exception as e:
            print(f"❌ Error updating {rel_path}: {e}")
            return False
    
    def categorize_files(self, files):
        """Categorize files by type for better reporting"""
        categories = {
            'Database': [],
            'Controllers': [],
            'Services': [],
            'Entities': [],
            'Configuration': [],
            'Dependencies': [],
            'Documentation': [],
            'Other': []
        }
        
        for file_path in files:
            file_str = str(file_path).lower()
            
            if 'entity' in file_str or file_str.endswith('.sql'):
                categories['Database'].append(file_path)
            elif 'controller' in file_str:
                categories['Controllers'].append(file_path)
            elif 'service' in file_str or 'mapper' in file_str:
                categories['Services'].append(file_path)
            elif file_str.endswith('pom.xml'):
                categories['Dependencies'].append(file_path)
            elif file_str.endswith(('.yml', '.yaml', '.properties')):
                categories['Configuration'].append(file_path)
            elif file_str.endswith(('.md', '.txt')):
                categories['Documentation'].append(file_path)
            else:
                categories['Other'].append(file_path)
        
        return categories
    
    def show_update_plan(self, files):
        """Show what will be updated"""
        categories = self.categorize_files(files)
        
        print(f"\n📋 UPDATE PLAN")
        print("=" * 50)
        print(f"Total files to update: {len(files)}")
        print()
        
        for category, file_list in categories.items():
            if file_list:
                print(f"📁 {category} ({len(file_list)} files):")
                for file_path in file_list[:5]:  # Show first 5
                    print(f"   • {file_path}")
                if len(file_list) > 5:
                    print(f"   ... and {len(file_list) - 5} more")
                print()
        
        return True
    
    def update_all_files(self, files):
        """Update all files"""
        success_count = 0
        failed_files = []
        
        print(f"\n🔄 Updating {len(files)} files...")
        
        for i, file_path in enumerate(files, 1):
            if i % 10 == 0 or i == len(files):
                print(f"   Progress: {i}/{len(files)} files")
            
            if self.update_file(file_path):
                success_count += 1
            else:
                failed_files.append(file_path)
        
        print(f"\n📊 Update Results:")
        print(f"   • Successfully updated: {success_count}")
        print(f"   • Failed: {len(failed_files)}")
        
        if failed_files:
            print(f"\n❌ Failed files:")
            for file_path in failed_files:
                print(f"   • {file_path}")
        
        return len(failed_files) == 0
    
    def show_git_status(self):
        """Show git status after update"""
        try:
            result = subprocess.run(['git', 'status', '--porcelain'], 
                                  cwd=self.current_path, 
                                  capture_output=True, 
                                  text=True)
            
            if result.returncode == 0:
                changes = result.stdout.strip().split('\n') if result.stdout.strip() else []
                
                print(f"\n📊 Git Status After Update:")
                print(f"   • Modified/Added files: {len(changes)}")
                
                if changes:
                    print(f"\n📝 Changed files (first 10):")
                    for change in changes[:10]:
                        if change.strip():
                            status = change[:2]
                            file_path = change[3:]
                            status_icon = {'M ': '🔄', 'A ': '➕', 'D ': '➖', '??': '🆕'}.get(status, '📝')
                            print(f"   {status_icon} {file_path}")
                    
                    if len(changes) > 10:
                        print(f"   ... and {len(changes) - 10} more files")
                
                return True
            
        except subprocess.CalledProcessError:
            print("ℹ️  Git not available for status check")
        
        return False
    
    def run_update(self):
        """Run the complete update process"""
        print("🔄 AUTO UPDATE FROM MANUFACTURER'S ZIP")
        print("=" * 50)
        print(f"Current path: {self.current_path}")
        print(f"Manufacturer zip: {self.zip_path}")
        print()
        
        # Step 1: Create backup
        print("📦 Step 1: Creating backup...")
        if not self.create_git_backup():
            print("❌ Backup failed. Aborting update.")
            return False
        
        # Step 2: Extract zip
        print("\n📂 Step 2: Extracting manufacturer's code...")
        if not self.extract_zip():
            return False
        
        # Step 3: Analyze files
        print("\n🔍 Step 3: Analyzing files to update...")
        files_to_update = self.get_files_to_update()
        
        if not files_to_update:
            print("❌ No files found to update")
            return False
        
        # Step 4: Show update plan
        self.show_update_plan(files_to_update)
        
        # Step 5: Confirm update
        response = input("\n❓ Proceed with update? (y/N): ").strip().lower()
        if response != 'y':
            print("❌ Update cancelled")
            return False
        
        # Step 6: Update files
        print("\n🚀 Step 6: Updating files...")
        success = self.update_all_files(files_to_update)
        
        # Step 7: Show results
        print("\n✅ Step 7: Update completed!")
        self.show_git_status()
        
        print(f"\n🎯 NEXT STEPS:")
        print(f"   1. Review changes: git diff")
        print(f"   2. Check specific files: git diff <filename>")
        print(f"   3. See all changed files: git status")
        print(f"   4. Commit changes: git add . && git commit -m 'Integrate manufacturer updates'")
        
        if self.backup_created:
            print(f"   5. If issues occur: git reset --hard HEAD~1 (to restore backup)")
        
        return success
    
    def cleanup(self):
        """Clean up temporary files"""
        if self.temp_dir and os.path.exists(self.temp_dir):
            shutil.rmtree(self.temp_dir)
            print("🧹 Cleaned up temporary files")

def main():
    current_path = "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools/device-util-demo"
    zip_path = "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools/device-util-demo.zip"
    
    if not os.path.exists(current_path):
        print(f"❌ Current path not found: {current_path}")
        return 1
    
    if not os.path.exists(zip_path):
        print(f"❌ Zip file not found: {zip_path}")
        return 1
    
    updater = AutoUpdater(current_path, zip_path)
    
    try:
        success = updater.run_update()
        return 0 if success else 1
    except KeyboardInterrupt:
        print("\n\n❌ Update cancelled by user")
        return 1
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        return 1
    finally:
        updater.cleanup()

if __name__ == "__main__":
    sys.exit(main())