<template>
    <div>
        <template v-for="(item, index) in menuList">
            <div :key="index">
                <Dropdown transfer v-if="item.children.length !== 1" placement="right-start" :key="index" @on-click="changeMenu">
                    <Button style="width: 100px;padding:10px 0;color:white;font-size:14px;" type="text">
                        <Icon :color="iconColor" :type="item.icon"></Icon>
                        {{ item.title }}
                    </Button>

                    <div v-if="item.menuGroup" slot="list">
                        <template v-for="(group, k) in item.menuGroup">
                            <DropdownMenu :key="k" style="width:150px;display:table-cell">
                                <Row v-if="group.title" class="menu-sub-title">
                                    <FontIcon :type="group.icon" size="20"></FontIcon>
                                    <span>{{ group.title }}</span>
                                </Row>
                                <template v-for="(child, i) in item.children" v-if="i >= group.start && i <= group.end" @click="handleClick">
                                    <DropdownItem :name="child.name" :key="i" :divided="child.divided" >
                                        <Icon :type="child.icon"></Icon>
                                        <span style="padding-left:10px;">{{ itemTitle(child) }}</span>
                                    </DropdownItem>
                                </template>
                            </DropdownMenu>
                        </template>
                    </div>
                    <DropdownMenu v-else style="width:150px;display:inline-block;" slot="list">
                        <template v-for="(child, i) in item.children">
                            <DropdownItem :name="child.name" :key="i" :divided="child.divided">
                                <Icon :type="child.icon"></Icon>
                                <span style="padding-left:10px;">{{ itemTitle(child) }}</span>
                            </DropdownItem>
                        </template>
                    </DropdownMenu>

                    
                </Dropdown>
                <Dropdown transfer v-else placement="right-start" :key="index" @on-click="changeMenu">
                    <Button @click="changeMenu(item.children[0].name)" style="width: 70px;margin-left: -5px;padding:10px 0;" type="text">
                        <Icon :color="iconColor" :type="item.children[0].icon || item.icon"></Icon>
                         {{ item.title }}
                    </Button>
                    <DropdownMenu style="width:150px;" slot="list">
                        <DropdownItem :name="item.children[0].name" :key="'d' + index"><Icon :type="item.children[0].icon || item.icon"></Icon><span style="padding-left:10px;">{{ itemTitle(item.children[0]) }}</span></DropdownItem>
                    </DropdownMenu>
                </Dropdown>
            </div>
        </template>
    </div>
</template>

<script>
export default {
  name: "sidebarMenuShrink",
  props: {
    menuList: {
      type: Array
    },
    iconColor: {
      type: String,
      default: "white"
    },
    menuTheme: {
      type: String,
      default: "darck"
    }
  },
  methods: {
    handleClick(name) {
      console.log(name);
    },
    changeMenu(active) {
      console.log("change");
      this.$emit("on-change", active);
    },
    itemTitle(item) {
      if (typeof item.title === "object") {
        return this.$t(item.title.i18n);
      } else {
        return item.title;
      }
    }
  }
};
</script>
