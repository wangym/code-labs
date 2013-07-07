filetype on
filetype plugin on
filetype indent on
syntax on "语法高亮
colorscheme murphy "配色方案
"autocmd GUIEnter * simalt ~x "启动后最大化
highlight Cursorline guibg=grey15 "当前背景高亮
autocmd FileType php set omnifunc=phpcomplete#CompletePHP
set hls "搜索高亮
set nobk "关闭文件备份
set number "显示行号
set ruler "右下显示行列
set autoread "Set to auto read when a file is changed from the outside
"set mousehide "输入隐藏鼠标
set showmatch "匹配括号之类
set incsearch "搜索自动定位
set cursorline "高亮当前行
set autoindent "自动缩进
set noexpandtab "在Tab处插入Tab符
set smartindent "智能缩进
set nocompatible "不兼容vi
set novisualbell "屏幕闪烁 visualbell|novisualbell
set mouse=a "鼠标模式v|a
set history=300 "Sets how many lines of history VIM has to remember
"set guifont=YaHei_Consolas_Hybrid:h10:cANSI "字体
"set columns=80 "设置屏幕的行数
set tabstop=4 "Tab空格数
set backspace=2 "backspace
set background=dark "背景颜色
set shiftwidth=4 "一个Tab
set fileformat=unix "文件风格
set guioptions-=m "不显示菜单
set guioptions-=T "不显示工具
set fenc=chinese "以下几个都是编码的问题，用来消除乱码的
set encoding=utf-8
set fileencodings=utf-8,chinese,latin-1
autocmd! bufwritepost _vimrc source %
map <C-n> :tabn<CR>
map <C-p> :tabp<CR>
map <C-c> :tabc<CR>
map <C-l> :NERDTreeToggle<CR>
